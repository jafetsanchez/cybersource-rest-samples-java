package samples.payments.coreServices;

import java.util.Properties;

import com.cybersource.authsdk.core.MerchantConfig;

import Api.PaymentsApi;
import Data.Configuration;
import Invokers.ApiClient;
import Invokers.ApiException;
import Model.CreatePaymentRequest;
import Model.PtsV2PaymentsPost201Response;
import Model.Ptsv2paymentsClientReferenceInformation;
import Model.Ptsv2paymentsOrderInformation;
import Model.Ptsv2paymentsOrderInformationAmountDetails;
import Model.Ptsv2paymentsOrderInformationBillTo;
import Model.Ptsv2paymentsPaymentInformation;
import Model.Ptsv2paymentsPaymentInformationCard;
import Model.Ptsv2paymentsPointOfSaleInformation;
import Model.Ptsv2paymentsProcessingInformation;

public class ProcessPayment {
	private static String responseCode = null;
	private static String status = null;
	private static PtsV2PaymentsPost201Response response;
	private static boolean capture = false;
	private static Properties merchantProp;

	private static CreatePaymentRequest request;

	private static CreatePaymentRequest getRequest(boolean capture) {
		request = new CreatePaymentRequest();

		Ptsv2paymentsClientReferenceInformation client = new Ptsv2paymentsClientReferenceInformation();
		client.code("test_payment");
		request.clientReferenceInformation(client);

		Ptsv2paymentsPointOfSaleInformation saleInformation = new Ptsv2paymentsPointOfSaleInformation();
		saleInformation.catLevel(6);
		saleInformation.terminalCapability(4);
		request.pointOfSaleInformation(saleInformation);

		Ptsv2paymentsOrderInformationBillTo billTo = new Ptsv2paymentsOrderInformationBillTo();
		billTo.country("US");
		billTo.firstName("Isabel");
		billTo.lastName("Sanchez");
		billTo.address1("5461 N East River Rd");
		billTo.postalCode("60656");
		billTo.locality("Chicago");
		billTo.administrativeArea("IL");
		billTo.email("isabel.sanchez@cubewave.com");

		Ptsv2paymentsOrderInformationAmountDetails amountDetails = new Ptsv2paymentsOrderInformationAmountDetails();
		amountDetails.totalAmount("450.00");
		amountDetails.currency("USD");

		Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation();
		orderInformation.billTo(billTo);
		orderInformation.amountDetails(amountDetails);
		request.setOrderInformation(orderInformation);

		Ptsv2paymentsProcessingInformation processingInformation = new Ptsv2paymentsProcessingInformation();
		if (capture == true) {
			processingInformation.capture(true);
		}
		request.processingInformation(processingInformation);

		Ptsv2paymentsPaymentInformationCard card = new Ptsv2paymentsPaymentInformationCard();
		card.expirationYear("2025");
		card.number("4111111111111111");
		card.securityCode("123");
		card.expirationMonth("1");

		Ptsv2paymentsPaymentInformation paymentInformation = new Ptsv2paymentsPaymentInformation();
		paymentInformation.card(card);
		request.setPaymentInformation(paymentInformation);

		return request;

	}

	public static void main(String args[]) throws Exception {
		process(capture);
	}

	public static PtsV2PaymentsPost201Response process(boolean check) throws Exception {

		try {
			capture = check;
			request = getRequest(capture);
			/* Read Merchant details. */
			merchantProp = Configuration.getMerchantDetails();
			MerchantConfig merchantConfig = new MerchantConfig(merchantProp);
			
			ApiClient apiClient = new ApiClient();
			
			apiClient.merchantConfig = merchantConfig;
			
			PaymentsApi paymentApi = new PaymentsApi(apiClient);
			response = paymentApi.createPayment(request);

			responseCode = apiClient.responseCode;
			status = apiClient.status;

			System.out.println("ResponseCode :" + responseCode);
			System.out.println("Status :" + status);
			System.out.println(response);

		} catch (ApiException e) {

			e.printStackTrace();
		}
		return response;
	}

}
