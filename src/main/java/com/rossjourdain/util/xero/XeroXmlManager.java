
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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import net.oauth.OAuthProblemException;

/**
 *
 * @author ross
 */
public class XeroXmlManager
{

    public static ArrayOfInvoice xmlToInvoices(InputStream invoiceStream)
            throws Exception
    {
        ArrayOfInvoice arrayOfInvoices = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ResponseType> element = unmarshaller.unmarshal(new StreamSource(invoiceStream), ResponseType.class);
            ResponseType response = element.getValue();
            arrayOfInvoices = response.getInvoices();
        }
        catch(JAXBException ex)
        {
            System.out.println("Error converting xml to Invoices");
            throw new Exception(ex);
        }
        return arrayOfInvoices;
    }

    public static ArrayOfContact xmlToContacts(InputStream invoiceStream)
            throws Exception
    {
        ArrayOfContact arrayOfContact = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ResponseType> element = unmarshaller.unmarshal(new StreamSource(invoiceStream), ResponseType.class);
            ResponseType response = element.getValue();
            arrayOfContact = response.getContacts();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            System.out.println("Error converting xml to Contacts");
            throw new Exception(ex);
        }
        return arrayOfContact;
    }

    public static ResponseType xmlToResponse(InputStream responseStream)
            throws Exception
    {
        ResponseType response = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ResponseType> element = unmarshaller.unmarshal(new StreamSource(responseStream), ResponseType.class);
            response = element.getValue();
        }
        catch(JAXBException ex)
        {
            System.out.println("Error converting xml to Response");
            throw new Exception(ex);
        }
        return response;
    }

    public static ApiExceptionExtended xmlToException(String exceptionString)

    {
        ApiExceptionExtended apiException = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ApiExceptionExtended.class.getPackage().getName(), ApiExceptionExtended.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement element = unmarshaller.unmarshal(new StreamSource(new StringReader(exceptionString)), ApiExceptionExtended.class);
            apiException = (ApiExceptionExtended)element.getValue();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
        }
        return apiException;
    }

    public static String oAuthProblemExceptionToXml(OAuthProblemException authProblemException)
    {
        String oAuthProblemExceptionString = null;

        Map<String, Object> params = authProblemException.getParameters();
        for (String key : params.keySet()) {
            Object o = params.get(key);
            if (key.contains("ApiException")) {
                oAuthProblemExceptionString = key + "=" + o.toString();
            }
        }

        return oAuthProblemExceptionString;
    }

    public static String contactsToXml(ArrayOfContact arrayOfContacts)
            throws Exception
    {
        String contactsString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            ObjectFactory factory = new ObjectFactory();
            JAXBElement element = factory.createContacts(arrayOfContacts);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            contactsString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting Contacts to XML", ex);
        }
        return contactsString;
    }

    public static String contactToXml(Contact arrayOfContacts)
            throws Exception
    {
        String accountsString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(Account.class.getPackage().getName(), Account.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            JAXBElement element = new JAXBElement(new QName(arrayOfContacts.getClass().getSimpleName().toString()), Contact.class, arrayOfContacts);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            accountsString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting Contact to XML", ex);
        }
        return accountsString;
    }

    public static String invoicesToXml(ArrayOfInvoice arrayOfInvoices)
            throws Exception
    {
        String invoicesString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            ObjectFactory factory = new ObjectFactory();
            JAXBElement element = factory.createInvoices(arrayOfInvoices);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            invoicesString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting Invoices to XML", ex);
        }
        return invoicesString;
    }

    public static String paymentsToXml(ArrayOfPayment arrayOfPayment)
            throws Exception
    {
        String paymentsString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            ObjectFactory factory = new ObjectFactory();
            JAXBElement element = factory.createPayments(arrayOfPayment);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            paymentsString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting Payments to XML", ex);
        }
        return paymentsString;
    }

    public static String accountToXml(Object arrayOfPayment)
            throws Exception
    {
        String accountsString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(Account.class.getPackage().getName());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            JAXBElement element = new JAXBElement(new QName("Account"), Account.class, (Account)arrayOfPayment);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            accountsString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting Account to XML", ex);
        }
        return accountsString;
    }

    public static String contactGroupToXml(ArrayOfContactGroup passedOject)
            throws Exception
    {
        String returnString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(passedOject.getClass().getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            JAXBElement element = new JAXBElement(new QName("ContactGroups"), ArrayOfContactGroup.class, passedOject);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            returnString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception("Error converting ContactGroup to XML", ex);
        }
        return returnString;
    }

    public static ArrayOfContactGroup xmlToContactGroups(InputStream bodyAsStream)
            throws Exception
    {
        ArrayOfContactGroup returnObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement element = unmarshaller.unmarshal(new StreamSource(bodyAsStream), ResponseType.class);
            ResponseType response = (ResponseType)element.getValue();
            returnObject = response.getContactGroups();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            System.out.println((new StringBuilder()).append("Error converting xml to ").append(returnObject.getClass().getSimpleName()).toString());
            throw new Exception(ex);
        }
        return returnObject;
    }

    public static String bankTransactionsToXml(ArrayOfBankTransaction passedObject)
            throws Exception
    {
        String returnString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(passedObject.getClass().getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            ObjectFactory factory = new ObjectFactory();
            JAXBElement element = factory.createBankTransactions(passedObject);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            returnString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            System.out.println("Error converting bankTransactions to XML");
            throw new Exception(ex);
        }
        return returnString;
    }

    public static ArrayOfBankTransaction xmltoBankTransactions(InputStream bodyAsStream)
            throws Exception
    {
        ArrayOfBankTransaction returnObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement element = unmarshaller.unmarshal(new StreamSource(bodyAsStream), ResponseType.class);
            ResponseType response = (ResponseType)element.getValue();
            returnObject = response.getBankTransactions();
        }
        catch(JAXBException ex)
        {
            ex.printStackTrace();
            throw new Exception((new StringBuilder()).append("Error converting xml to ").append(returnObject.getClass().getSimpleName()).toString(), ex);
        }
        return returnObject;
    }

    public static String itemsToXml(ArrayOfItem passedObject)
            throws Exception
    {
        String returnString = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(passedObject.getClass().getPackage().getName(), ResponseType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            ObjectFactory factory = new ObjectFactory();
            JAXBElement element = factory.createItems(passedObject);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            returnString = stringWriter.toString();
        }
        catch(JAXBException ex)
        {
            throw new Exception(ex);
        }
        return returnString;
    }

    public static ArrayOfItem xmlToItems(InputStream bodyAsStream)
            throws Exception
    {
        ArrayOfItem returnObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(ResponseType.class.getPackage().getName(), ResponseType.class.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement element = unmarshaller.unmarshal(new StreamSource(bodyAsStream), ResponseType.class);
            ResponseType response = (ResponseType)element.getValue();
            returnObject = response.getItems();
        }
        catch(JAXBException ex)
        {
            throw new Exception(ex);
        }
        return returnObject;
    }
}
