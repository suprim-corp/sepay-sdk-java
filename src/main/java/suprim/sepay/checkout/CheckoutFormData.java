package suprim.sepay.checkout;

import java.util.Collections;
import java.util.Map;

/**
 * Contains all data needed to render a checkout form.
 */
public class CheckoutFormData {

    private final String actionUrl;
    private final Map<String, String> formFields;

    public CheckoutFormData(String actionUrl, Map<String, String> formFields) {
        this.actionUrl = actionUrl;
        this.formFields = formFields;
    }

    /**
     * Returns the form action URL.
     */
    public String getActionUrl() {
        return actionUrl;
    }

    /**
     * Returns all form fields including signature.
     */
    public Map<String, String> getFormFields() {
        return Collections.unmodifiableMap(formFields);
    }

    /**
     * Returns the signature value.
     */
    public String getSignature() {
        return formFields.get("signature");
    }
}
