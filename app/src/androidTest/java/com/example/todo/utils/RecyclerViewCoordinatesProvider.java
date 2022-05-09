package com.example.todo.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralLocation;

import java.util.Objects;

public class RecyclerViewCoordinatesProvider implements CoordinatesProvider {
    private final int position;
    private final Direction direction;

    public enum Direction{
        NONE, TOP, DOWN
    }

    public RecyclerViewCoordinatesProvider(int position,Direction direction ){
        this.position = position;
        this.direction = direction;
    }

    @Override
    public float[] calculateCoordinates(View view) {
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        float[] coordinates = GeneralLocation.CENTER.calculateCoordinates(Objects.requireNonNull(((RecyclerView) (view.getParent())).getLayoutManager()).findViewByPosition(position));
        switch(direction){
            case DOWN:
                coordinates[1] += view.getHeight() * 1.5f;
                break;
            case TOP:
                coordinates[1] -= view.getHeight() * 1.5f;
                break;
        }
        return coordinates;
    }
}
