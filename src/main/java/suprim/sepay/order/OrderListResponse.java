package suprim.sepay.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Paginated response for order list API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderListResponse {

    private List<Order> data;
    private int total;
    private int page;

    @JsonProperty("per_page")
    private int perPage;

    @JsonProperty("total_pages")
    private int totalPages;

    // Default constructor for Jackson
    public OrderListResponse() {}

    // Getters
    public List<Order> getData() {
        return nonNull(data) ? Collections.unmodifiableList(data) : Collections.emptyList();
    }

    public int getTotal() { return total; }
    public int getPage() { return page; }
    public int getPerPage() { return perPage; }
    public int getTotalPages() { return totalPages; }

    /**
     * Returns true if there is a next page.
     */
    public boolean hasNextPage() {
        return page < totalPages;
    }

    /**
     * Returns true if there is a previous page.
     */
    public boolean hasPrevPage() {
        return page > 1;
    }

    // Setters for Jackson
    public void setData(List<Order> data) { this.data = data; }
    public void setTotal(int total) { this.total = total; }
    public void setPage(int page) { this.page = page; }
    public void setPerPage(int perPage) { this.perPage = perPage; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
