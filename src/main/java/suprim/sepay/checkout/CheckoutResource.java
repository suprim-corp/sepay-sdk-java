package suprim.sepay.checkout;

import suprim.sepay.auth.SignatureGenerator;
import suprim.sepay.config.Environment;
import suprim.sepay.config.UrlConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Resource for generating checkout forms and URLs.
 */
public class CheckoutResource {

    private final Environment environment;
    private final SignatureGenerator signatureGenerator;
    private final String customCheckoutBaseUrl;

    /**
     * Creates checkout resource with environment only (no signature verification).
     */
    public CheckoutResource(Environment environment) {
        this(environment, null, null);
    }

    /**
     * Creates checkout resource with signature verification support.
     *
     * @param environment the environment
     * @param secretKey secret key for signature operations (nullable)
     */
    public CheckoutResource(Environment environment, String secretKey) {
        this(environment, secretKey, null);
    }

    /**
     * Creates checkout resource with all options.
     *
     * @param environment the environment
     * @param secretKey secret key for signature operations (nullable)
     * @param customCheckoutBaseUrl custom checkout URL (nullable)
     */
    public CheckoutResource(Environment environment, String secretKey, String customCheckoutBaseUrl) {
        this.environment = nonNull(environment) ? environment : Environment.SANDBOX;
        this.signatureGenerator = nonNull(secretKey) && !secretKey.isEmpty()
            ? new SignatureGenerator(secretKey) : null;
        this.customCheckoutBaseUrl = customCheckoutBaseUrl;
    }

    /**
     * Generates form data for the checkout request.
     *
     * @param request the checkout request
     * @return form data with action URL and fields
     */
    public CheckoutFormData generateForm(CheckoutRequest request) {
        String actionUrl = getCheckoutUrl();
        Map<String, String> fields = request.toFormFields();
        return new CheckoutFormData(actionUrl, fields);
    }

    /**
     * Returns the checkout URL for the current environment.
     */
    public String getCheckoutUrl() {
        return nonNull(customCheckoutBaseUrl)
            ? customCheckoutBaseUrl + "/v1/checkout/init"
            : UrlConfig.getCheckoutInitUrl(environment);
    }

    /**
     * Builds an HTML form string for embedding.
     *
     * @param request      the checkout request
     * @param submitLabel  label for submit button
     * @return HTML form string
     */
    public String buildHtmlForm(CheckoutRequest request, String submitLabel) {
        CheckoutFormData formData = generateForm(request);
        StringBuilder html = new StringBuilder();

        html.append("<form method=\"POST\" action=\"")
                .append(escapeHtml(formData.getActionUrl()))
                .append("\">\n");

        for (Map.Entry<String, String> field : formData.getFormFields().entrySet()) {
            html.append("    <input type=\"hidden\" name=\"")
                    .append(escapeHtml(field.getKey()))
                    .append("\" value=\"")
                    .append(escapeHtml(field.getValue()))
                    .append("\">\n");
        }

        html.append("    <button type=\"submit\">")
                .append(escapeHtml(submitLabel))
                .append("</button>\n");
        html.append("</form>");

        return html.toString();
    }

    /**
     * Builds an HTML form with default submit label.
     */
    public String buildHtmlForm(CheckoutRequest request) {
        return buildHtmlForm(request, "Pay Now");
    }

    private String escapeHtml(String text) {
        if (isNull(text)) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Verifies a checkout signature using constant-time comparison.
     * Use this to validate callbacks/redirects from SePay.
     *
     * @param fields form fields without signature
     * @param providedSignature signature to verify
     * @return true if signature is valid
     * @throws IllegalStateException if no secret key was provided
     */
    public boolean verifySignature(Map<String, String> fields, String providedSignature) {
        if (isNull(signatureGenerator)) {
            throw new IllegalStateException(
                "Cannot verify signature: no secret key provided to CheckoutResource");
        }
        if (isNull(providedSignature) || providedSignature.isEmpty()) {
            return false;
        }
        String expectedSignature = signatureGenerator.generateSignature(fields);
        return MessageDigest.isEqual(
            expectedSignature.getBytes(StandardCharsets.UTF_8),
            providedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates JavaScript for auto-submitting the checkout form.
     *
     * @param formId the HTML form ID to submit
     * @return JavaScript code as a string
     */
    public String generateAutoSubmitScript(String formId) {
        String safeFormId = isNull(formId) || formId.isEmpty() ? "sepay-checkout-form" : formId;
        return String.format(
            "<script>document.getElementById(\"%s\").submit();</script>",
            escapeHtml(safeFormId)
        );
    }

    /**
     * Generates JavaScript for auto-submitting with default form ID.
     */
    public String generateAutoSubmitScript() {
        return generateAutoSubmitScript("sepay-checkout-form");
    }

    /**
     * Builds HTML form with auto-submit script.
     *
     * @param request checkout request
     * @param formId HTML form ID
     * @return complete HTML with form and auto-submit script
     */
    public String buildAutoSubmitForm(CheckoutRequest request, String formId) {
        String safeFormId = isNull(formId) || formId.isEmpty() ? "sepay-checkout-form" : formId;

        CheckoutFormData formData = generateForm(request);
        StringBuilder html = new StringBuilder();

        html.append("<form id=\"")
            .append(escapeHtml(safeFormId))
            .append("\" method=\"POST\" action=\"")
            .append(escapeHtml(formData.getActionUrl()))
            .append("\">\n");

        for (Map.Entry<String, String> field : formData.getFormFields().entrySet()) {
            html.append("    <input type=\"hidden\" name=\"")
                .append(escapeHtml(field.getKey()))
                .append("\" value=\"")
                .append(escapeHtml(field.getValue()))
                .append("\">\n");
        }

        html.append("</form>\n");
        html.append(generateAutoSubmitScript(safeFormId));

        return html.toString();
    }

    /**
     * Builds HTML form with auto-submit using default form ID.
     */
    public String buildAutoSubmitForm(CheckoutRequest request) {
        return buildAutoSubmitForm(request, "sepay-checkout-form");
    }
}
