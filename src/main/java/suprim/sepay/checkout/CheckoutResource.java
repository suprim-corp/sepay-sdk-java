package suprim.sepay.checkout;

import suprim.sepay.config.Environment;
import suprim.sepay.config.UrlConfig;

import java.util.Map;

/**
 * Resource for generating checkout forms and URLs.
 */
public class CheckoutResource {

    private final Environment environment;

    public CheckoutResource(Environment environment) {
        this.environment = environment != null ? environment : Environment.SANDBOX;
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
        return UrlConfig.getCheckoutInitUrl(environment);
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
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
