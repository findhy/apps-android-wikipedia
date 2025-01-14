package org.wikipedia.pageimages;

import android.content.ContentValues;
import android.database.Cursor;
import org.wikipedia.PageTitle;
import org.wikipedia.Site;
import org.wikipedia.data.PersistanceHelper;

public class PageImagePersistanceHelper extends PersistanceHelper<PageImage> {
    @Override
    public PageImage fromCursor(Cursor c) {
        // Carefully, get them back by using position only
        Site site = new Site(c.getString(1));
        // FIXME: Does not handle non mainspace pages
        PageTitle title = new PageTitle(null, c.getString(2), site);
        String imageName = c.getString(3);
        return new PageImage(title, imageName);
    }

    @Override
    protected ContentValues toContentValues(PageImage obj) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("site", obj.getTitle().getSite().getDomain());
        contentValues.put("title", obj.getTitle().getPrefixedText());
        contentValues.put("imageName", obj.getImageName());
        return contentValues;
    }

    @Override
    public String getTableName() {
        return "pageimages";
    }

    @Override
    public Column[] getColumnsAdded(int version) {
        switch (version) {
            case 1:
                return new Column[] {
                        new Column("_id", "integer primary key"),
                        new Column("site", "string"),
                        new Column("title", "string"),
                        new Column("imageName", "string"),
                };
            default:
                return new Column[0];
        }
    }

    @Override
    protected String getPrimaryKeySelection() {
        return "site = ? AND title = ?";
    }

    @Override
    protected String[] getPrimaryKeySelectionArgs(PageImage obj) {
        return new String[] {
                obj.getTitle().getSite().getDomain(),
                obj.getTitle().getPrefixedText()
        };
    }
}
