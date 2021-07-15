package com.example.testmovies.adapters.items;

public interface ItemType {
    int TYPE_HEADER = 0;
    int TYPE_ITEM = 1;
    int TYPE_FOOTER = 2;
    int TYPE_MUTABLE = 3;

    int getItemType();
}
