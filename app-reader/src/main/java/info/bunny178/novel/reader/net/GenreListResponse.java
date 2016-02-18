package info.bunny178.novel.reader.net;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import info.bunny178.novel.reader.model.Genre;
import info.bunny178.novel.reader.model.Novel;

/**
 *
 * @author ISHIMARU Sohei on 2015/09/11.
 */
@Root(name = "genres")
public class GenreListResponse {

    @ElementList(inline = true, name = "genres", type = Genre.class, required = false)
    private List<Genre> mGenreList;

    public GenreListResponse() {
        /* NOP */
    }

    public List<Genre> getGenreList() {
        return mGenreList;
    }
}
