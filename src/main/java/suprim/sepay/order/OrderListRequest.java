package suprim.sepay.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Request parameters for listing orders.
 */
public class OrderListRequest {

    private static final int DEFAULT_PER_PAGE = 20;
    private static final int DEFAULT_PAGE = 1;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final Integer perPage;
    private final Integer page;
    private final String query;
    private final String customerId;
    private final OrderStatus orderStatus;
    private final LocalDate fromCreatedAt;
    private final LocalDate toCreatedAt;
    private final String sort;

    private OrderListRequest(Builder builder) {
        this.perPage = builder.perPage;
        this.page = builder.page;
        this.query = builder.query;
        this.customerId = builder.customerId;
        this.orderStatus = builder.orderStatus;
        this.fromCreatedAt = builder.fromCreatedAt;
        this.toCreatedAt = builder.toCreatedAt;
        this.sort = builder.sort;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts request to query parameters map.
     */
    public Map<String, String> toQueryParams() {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("per_page", String.valueOf(nonNull(perPage) ? perPage : DEFAULT_PER_PAGE));
        params.put("page", String.valueOf(nonNull(page) ? page : DEFAULT_PAGE));

        if (nonNull(query) && !query.isEmpty()) {
            params.put("query", query);
        }
        if (nonNull(customerId) && !customerId.isEmpty()) {
            params.put("customer_id", customerId);
        }
        if (nonNull(orderStatus)) {
            params.put("order_status", orderStatus.getValue());
        }
        if (nonNull(fromCreatedAt)) {
            params.put("from_created_at", fromCreatedAt.format(DATE_FORMAT));
        }
        if (nonNull(toCreatedAt)) {
            params.put("to_created_at", toCreatedAt.format(DATE_FORMAT));
        }
        if (nonNull(sort) && !sort.isEmpty()) {
            params.put("sort", sort);
        }

        return params;
    }

    // Getters
    public Integer getPerPage() { return nonNull(perPage) ? perPage : DEFAULT_PER_PAGE; }
    public Integer getPage() { return nonNull(page) ? page : DEFAULT_PAGE; }
    public String getQuery() { return query; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public LocalDate getFromCreatedAt() { return fromCreatedAt; }
    public LocalDate getToCreatedAt() { return toCreatedAt; }
    public String getSort() { return sort; }

    public static class Builder {
        private Integer perPage;
        private Integer page;
        private String query;
        private String customerId;
        private OrderStatus orderStatus;
        private LocalDate fromCreatedAt;
        private LocalDate toCreatedAt;
        private String sort;

        public Builder perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.orderStatus = status;
            return this;
        }

        public Builder fromDate(LocalDate from) {
            this.fromCreatedAt = from;
            return this;
        }

        public Builder toDate(LocalDate to) {
            this.toCreatedAt = to;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public OrderListRequest build() {
            return new OrderListRequest(this);
        }
    }
}
