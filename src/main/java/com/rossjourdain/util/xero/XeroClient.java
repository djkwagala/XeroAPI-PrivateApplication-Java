
/*
 *  Copyright 2011 Ross Jourdain
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.rossjourdain.util.xero;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.signature.RSA_SHA1;

/**
 *
 * @author ross
 */
public class XeroClient
{

    public XeroClient(String endpointUrl, String consumerKey, String consumerSecret, String privateKey)
    {
        this.endpointUrl = endpointUrl;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.privateKey = privateKey;
    }

    public XeroClient(XeroClientProperties clientProperties)
    {
        endpointUrl = clientProperties.getEndpointUrl();
        consumerKey = clientProperties.getConsumerKey();
        consumerSecret = clientProperties.getConsumerSecret();
        privateKey = clientProperties.getPrivateKey();
    }

    public OAuthAccessor buildAccessor()
    {
        OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, null, null);
        consumer.setProperty(RSA_SHA1.PRIVATE_KEY, privateKey);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.accessToken = consumerKey;
        accessor.tokenSecret = consumerSecret;
        return accessor;
    }

    public ArrayOfInvoice getInvoices()
            throws Exception
    {
        ArrayOfInvoice arrayOfInvoices = null;
        try
        {
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.GET, (new StringBuilder()).append(endpointUrl).append("Invoices").toString(), null);
            arrayOfInvoices = XeroXmlManager.xmlToInvoices(response.getBodyAsStream());
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
        return arrayOfInvoices;
    }

    public Report getReport(String reportUrl)
            throws Exception
    {
        Report report = null;
        try
        {
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.GET, (new StringBuilder()).append(endpointUrl).append("Reports").append(reportUrl).toString(), null);
            ResponseType responseType = XeroXmlManager.xmlToResponse(response.getBodyAsStream());
            if(responseType != null && responseType.getReports() != null && responseType.getReports().getReport() != null && responseType.getReports().getReport().size() > 0)
                report = (Report)responseType.getReports().getReport().get(0);
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
        return report;
    }

    public ArrayOfContact postContacts(ArrayOfContact arrayOfContact)
            throws Exception
    {
        try
        {
            String contactsString = XeroXmlManager.contactsToXml(arrayOfContact);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Contacts").toString(), OAuth.newList(new String[] {
                    "xml", contactsString
            }));
            response1 = response;
            ArrayOfContact arrayOfAccountResponse = XeroXmlManager.xmlToContacts(response.getBodyAsStream());
            return arrayOfAccountResponse;
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public Contact postContact(Contact arrayOfContact)
            throws Exception
    {
        try
        {
            String contactsString = XeroXmlManager.contactToXml(arrayOfContact);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Contacts").toString(), OAuth.newList(new String[]{
                    "xml", contactsString
            }));
            ArrayOfContact responseContact = XeroXmlManager.xmlToContacts(response.getBodyAsStream());
            return (Contact)responseContact.getContact().get(0);
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public void postAccount(Account arrayOfAccount)
            throws Exception
    {
        try
        {
            String contactsString = XeroXmlManager.accountToXml(arrayOfAccount);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Accounts").toString(), OAuth.newList(new String[] {
                    "xml", contactsString
            }));
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public void postInvoices(ArrayOfInvoice arrayOfInvoices)
            throws Exception
    {
        try
        {
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            String contactsString = XeroXmlManager.invoicesToXml(arrayOfInvoices);
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Invoices?summarizeErrors=false").toString(), OAuth.newList(new String[]{
                    "xml", contactsString
            }));
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public void postPayments(ArrayOfPayment arrayOfPayment)
            throws XeroClientException, Exception
    {
        try
        {
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            String paymentsString = XeroXmlManager.paymentsToXml(arrayOfPayment);
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Payments").toString(), OAuth.newList(new String[] {
                    "xml", paymentsString
            }));
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public ArrayOfBankTransaction postBankTransactions(ArrayOfBankTransaction arrayOfBankTransaction, String endpointUrlExtension)
            throws Exception
    {
        try
        {
            String xmlString = XeroXmlManager.bankTransactionsToXml(arrayOfBankTransaction);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("BankTransactions").append(endpointUrlExtension).toString(), OAuth.newList(new String[] {
                    "xml", xmlString
            }));
            ArrayOfBankTransaction returnResponse = XeroXmlManager.xmltoBankTransactions(response.getBodyAsStream());
            return returnResponse;
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public ArrayOfItem postItems(ArrayOfItem arrayOfBankTransaction, String endpointUrlExtension)
            throws Exception
    {
        try
        {
            String xmlString = XeroXmlManager.itemsToXml(arrayOfBankTransaction);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("Items").append(endpointUrlExtension).toString(), OAuth.newList(new String[] {
                    "xml", xmlString
            }));
            ArrayOfItem returnResponse = XeroXmlManager.xmlToItems(response.getBodyAsStream());
            return returnResponse;
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public ArrayOfContact getContacts(String endpointUrlExtension, Date modifiedSince)
    {
        ArrayOfContact returnObject = new ArrayOfContact();
        ArrayOfContact responseContacts = new ArrayOfContact();
        int pageNumber = 0;
        try{

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            do
            {
                pageNumber++;
                System.out.println((new StringBuilder()).append("Page:").append(pageNumber).toString());
                OAuthMessage response = client.invoke(accessor, OAuthMessage.GET, (new StringBuilder()).append(endpointUrl).append("Contacts").append(endpointUrlExtension).toString(), OAuth.newList(new String[] {
                        "If-Modified-Since", dateFormat.format(modifiedSince), "page", String.valueOf(pageNumber)
                }));
                responseContacts = XeroXmlManager.xmlToContacts(response.getBodyAsStream());
                System.out.println(dateFormat.format(modifiedSince));

                for (Contact contact:responseContacts.getContact()){
                    returnObject.getContact().add(contact);
                }
            /*Contact contact;
            for(Iterator i$ = responseContacts.getContact().iterator(); i$.hasNext(); returnObject.getContact().add(contact))
                contact = (Contact)i$.next();*/

            } while(responseContacts.getContact().size() == 100);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return returnObject;
    }

    public ArrayOfContact getContacts(String commaSeparatedParameters)
            throws Exception
    {
        ArrayOfContact returnObject = new ArrayOfContact();
        ArrayOfContact responseContacts = new ArrayOfContact();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        OAuthClient client = new OAuthClient(new HttpClient3());
        OAuthAccessor accessor = buildAccessor();
        OAuthMessage response = client.invoke(accessor, OAuthMessage.GET, (new StringBuilder()).append(endpointUrl).append("Contacts").toString(), OAuth.newList(new String[] {
                commaSeparatedParameters
        }));
        responseContacts = XeroXmlManager.xmlToContacts(response.getBodyAsStream());
        return responseContacts;
    }

    public ArrayOfContact getContact(String identifier)
            throws Exception
    {
        OAuthClient client = new OAuthClient(new HttpClient3());
        OAuthAccessor accessor = buildAccessor();
        OAuthMessage response = client.invoke(accessor, OAuthMessage.GET, (new StringBuilder()).append(endpointUrl).append("Contacts/").append(identifier).toString(), null);
        ArrayOfContact responseContacts = XeroXmlManager.xmlToContacts(response.getBodyAsStream());
        return responseContacts;
    }

    public ArrayOfContactGroup postContactGroup(ArrayOfContactGroup arrayOfContactGroup)
            throws Exception, XeroClientUnexpectedException
    {
        try
        {
            String contactsString = XeroXmlManager.contactGroupToXml(arrayOfContactGroup);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, (new StringBuilder()).append(endpointUrl).append("ContactGroups").toString(), OAuth.newList(new String[] {
                    "xml", contactsString
            }));
            ArrayOfContactGroup responseContactGroups = XeroXmlManager.xmlToContactGroups(response.getBodyAsStream());
            return responseContactGroups;
        }
        catch(OAuthProblemException ex)
        {
            throw new Exception(ex);
        }
    }

    public File getInvoiceAsPdf(String invoiceId)
            throws Exception
    {

        File file = null;
        InputStream in = null;
        FileOutputStream out = null;

        try {

            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();

            OAuthMessage request = accessor.newRequestMessage(OAuthMessage.GET, endpointUrl + "Invoices" + "/" + invoiceId, null);
            request.getHeaders().add(new OAuth.Parameter("Accept", "application/pdf"));
            OAuthResponseMessage response = client.access(request, ParameterStyle.BODY);


            file = new File("Invoice-" + invoiceId + ".pdf");

            if (response != null && response.getHttpResponse() != null && (response.getHttpResponse().getStatusCode() / 2) != 2) {
                in = response.getBodyAsStream();
                out = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } else {
                throw response.toOAuthProblemException();
            }

        } catch(OAuthProblemException ex)
        {
            throw new Exception((new StringBuilder()).append("Error getting PDF of invoice ").append(invoiceId).toString(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ex) {
            }
        }
        return file;
    }

    public void setConsumerKey(String consumerKey)
    {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret)
    {
        this.consumerSecret = consumerSecret;
    }

    public void setEndpointUrl(String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }

    private String endpointUrl;
    private String consumerKey;
    private String consumerSecret;
    private String privateKey;
    OAuthMessage response1;
}
