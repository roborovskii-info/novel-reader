package info.bunny178.novel.reader.net;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import info.bunny178.novel.reader.model.Novel;

/**
 *
 * @author ISHIMARU Sohei on 2015/09/11.
 */
@Root(name = "response")
public class NovelListResponse {

    public static final String RESULT_OK = "OK";

    @Element(name = "response_status")
    private String mResponseStatus;

    @Element(name = "page_number", required = false)
    private int mPageNumber;

    @Element(name = "adult", required = false)
    private int mRating;

    @Element(name = "all_count", required = false)
    private int mTotalCount;

    @ElementList(name = "bookdatas", type = Novel.class, required = false)
    private List<Novel> mNovelList;

    public NovelListResponse() {
        /* NOP */
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public boolean isSuccess() {
        return RESULT_OK.equals(mResponseStatus);
    }

    public List<Novel> getNovelList() {
        return mNovelList;
    }
}
