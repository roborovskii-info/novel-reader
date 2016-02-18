package info.bunny178.novel.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author ISHIMARU Sohei on 2015/09/18.
 */
@Root(name = "response")
public class DownloadData {

    @Element(name = "id")
    private int mNovelId;

    @ElementList(name = "chapters")
    private List<Chapter> mChapterList;

    public int getNovelId() {
        return mNovelId;
    }

    public List<Chapter> getChapterList() {
        return mChapterList;
    }
}
