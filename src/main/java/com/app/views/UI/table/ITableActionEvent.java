package com.app.views.UI.table;

/**
 *
 * @author InuHa
 */
public interface ITableActionEvent {

    public void onEdit(int row);

    public void onDelete(int row);

    public void onView(int row);
    
}
