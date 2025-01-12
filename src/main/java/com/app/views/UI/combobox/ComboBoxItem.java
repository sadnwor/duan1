package com.app.views.UI.combobox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author InuHa
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComboBoxItem<T> {

    private String text;

    private T value;

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComboBoxItem<?> that = (ComboBoxItem<?>) obj;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
    
}
