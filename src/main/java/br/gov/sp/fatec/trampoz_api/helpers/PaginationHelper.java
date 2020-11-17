package br.gov.sp.fatec.trampoz_api.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class PaginationHelper {
    private Integer page;
    private Integer limit;
    private Long totalItems;

    public PaginationHelper(Integer page, Integer limit) {
        setPage(page);
        setLimit(limit);
        setTotalItems(0L);
    }

    public Integer getOffset() {
        return (getPage() - 1) * getLimit();
    }

    public Long getTotalPages() {
        Double totalPages = Math.ceil(getTotalItems() / getLimit().doubleValue());
        return totalPages.longValue();
    }

    public Boolean hasPreviousPage() {
        int previous = getPage() - 1;
        boolean PREVIOUS_IS_POSITIVE = previous > 0;
        boolean PREVIOUS_IS_NOT_GREATER_THAN_TOTAL = !(previous > getTotalPages());
        return PREVIOUS_IS_POSITIVE && PREVIOUS_IS_NOT_GREATER_THAN_TOTAL;
    }

    public Boolean hasNextPage() {
        int nextPage = getPage() + 1;
        long totalPages = getTotalPages();
        return nextPage <= totalPages;
    }

    public ObjectNode createJsonWithPaginationInformation() {
        return new ObjectMapper()
                .createObjectNode()
                .put("pages", getTotalPages())
                .put("hasPreviousPage", hasPreviousPage())
                .put("hasNextPage", hasNextPage());
    }
}
