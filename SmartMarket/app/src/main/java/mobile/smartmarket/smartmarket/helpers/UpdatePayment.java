package mobile.smartmarket.smartmarket.helpers;

import mobile.smartmarket.smartmarket.beans.BasicDataBean;

/**
 * Created by omar on 12/6/16.
 */
public class UpdatePayment {
    private static UpdatePayment ourInstance = new UpdatePayment();

    public static UpdatePayment getInstance() {
        return ourInstance;
    }

    private UpdatePayment() {
    }

    public Boolean process(BasicDataBean basicDataBean, boolean isAccepted){
        Boolean response = false;

        //TODO Llamar la rutina de pago
        response = true;

        return response;
    }
}
