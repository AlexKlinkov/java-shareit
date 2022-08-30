package ru.practicum.shareit.booking.MyPageable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

public class MyPageable implements Pageable {

    private final int offset;
    private final int limit;
    private final Sort sort;
    private static final int DEFAULT_PAGE_SIZE = 20;

    protected MyPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(Integer from, Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = DEFAULT_PAGE_SIZE;
        }
        return new MyPageable(from, size, Sort.unsorted());
    }

    public static Pageable of(Integer from, Integer size, Sort sort) {
        if (from == null && size == null) {
            from = 0;
            size = DEFAULT_PAGE_SIZE;
        }
        return new MyPageable(from, size, sort);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new MyPageable(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new MyPageable(offset, limit, sort);
    }

    @Override
    public Pageable first() {
        return new MyPageable(offset, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new MyPageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }
}
