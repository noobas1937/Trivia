/** Created by Jack Chen at 9/15/2014 */
package com.ecnu.trivia.common.component;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.ibatis.session.RowBounds;

/**
 * 描述分页组件,以用于具体的分页描述
 *
 * @author Jack Chen
 */
public class Page extends RowBounds implements Cloneable {
    /** 默认每页记录数 */
    public static final int PAGE_SIZE_DEFAULT = 20;

    public static final Page NO_PAGE = new Page(NO_ROW_OFFSET, NO_ROW_LIMIT) {
        @Override
        public void setPageSize(int pageSize) {
            throw new UnsupportedOperationException("不支持此操作");
        }

        @Override
        public void setCurrentPage(int currentPage) {
            throw new UnsupportedOperationException("不支持此操作");
        }

        @Override
        public void setTotalCount(int totalCount) {
            throw new UnsupportedOperationException("不支持此操作");
        }
    };
    /** 当前界面 */
    private int currentPage = 1;
    /** 每页显示的记录数,默认20条 */
    private int pageSize = PAGE_SIZE_DEFAULT;
    /** 总记录数 */
    private int totalCount;

    public Page() {
    }

    public Page(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if(currentPage <= 0) {
            return;
        }
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if(pageSize <= 0) {
            return;
        }
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    /** 是否无记录 */
    public boolean isEmpty() {
        return totalCount == 0;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /** 得到查询记录的起始点,即前一页记录点的最后一条 */
    @JSONField(serialize = false)
    public int getStartIndex() {
        return (currentPage - 1) * pageSize;
    }

    /** 得到查询记录结束点 */
    @JSONField(serialize = false)
    public int getEndIndex() {
        return currentPage * pageSize;
    }

    /** 总页数 */
    public long getTotalPage() {
        long i = totalCount / (long) pageSize;
        long j = totalCount % (long) pageSize;
        return i + (j == 0 ? 0 : 1);
    }

    /** 是否有上一页 */
    public boolean isPreviousExist() {
        return currentPage > 1;
    }

    /** 是否有下一页 */
    public boolean isNextExist() {
        return currentPage < getTotalPage();
    }

    /** 重写父类的相应方法,以提供自实现取得起始点 */
    @JSONField(serialize = false)
    @Override
    public int getOffset() {
        return getStartIndex();
    }

    /** 重写父类的相应方法,以提供自实现限制 */
    @JSONField(serialize = false)
    @Override
    public int getLimit() {
        return getPageSize();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Page page = (Page) o;

        if(currentPage != page.currentPage) {
            return false;
        }
        if(pageSize != page.pageSize) {
            return false;
        }
        return totalCount == page.totalCount;

    }

    @Override
    public int hashCode() {
        int result = currentPage;
        result = 31 * result + pageSize;
        result = 31 * result + totalCount;
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page clone() {
        try{
            return (Page) super.clone();
        } catch(CloneNotSupportedException ignore) {
        }

        return null;
    }

    @Override
    public String toString() {
        return "Page{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                '}';
    }
}
