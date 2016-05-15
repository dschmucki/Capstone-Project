package com.example.catcha.widget;

import android.view.View;

public class EmptyViewController {

    private final View contentView;
    private final View emptyView;
    private boolean isEmpty;


    public EmptyViewController(View contentView, View emptyView) {
        this.contentView = contentView;
        this.emptyView = emptyView;
    }

    public void setEmpty(boolean isEmpty) {
        if (this.isEmpty == isEmpty) {
            return;
        }
        this.isEmpty = isEmpty;
        emptyView.setVisibility(this.isEmpty ? View.VISIBLE : View.GONE);
        contentView.setVisibility(this.isEmpty ? View.GONE : View.VISIBLE);
    }

}
