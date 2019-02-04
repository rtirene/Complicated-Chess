package com.mobileapp.polimi.maprojectg4.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.model.FrozenPieces;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;

import java.util.Vector;


public class BoardAdapter extends BaseAdapter {

    private Context context;
    private Board board;
    private FrozenPieces frozenPieces;

    public BoardAdapter(Context c, Board board, FrozenPieces frozenPieces) {
        this.context = c;
        this.board = board;
        this.frozenPieces = frozenPieces;
    }

    @Override
    public int getCount() {
        return board.getSize()*board.getSize();
    }

    @Override
    public Object getItem(int position) {
        return board.getPieceAtPosition(new Position(position / board.getSize() + 1, position % board.getSize() + 1));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView v = new ImageView(context);
        if (convertView == null) {
            int size;

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                size = ViewUtils.getSizeOfBoard(context) / 6;
            else size = ViewUtils.getSizeOfBoard(context) /6;

            v.setLayoutParams(new GridView.LayoutParams(size, size));


            int col = position / 6 % 2;
            if (col == 0) {
                if (position % 2 == 0) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.board1));

                } else {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.board2));
                }
            } else {
                if (position % 2 == 0) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.board2));

                } else {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.board1));

                }
            }

            //frozen pieces
            if(frozenPieces.isBlackFrozen() &&
                    position == ((frozenPieces.getFrozenBlack().getRow()-1)*6)+frozenPieces.getFrozenBlack().getColumn()-1)
                v.setBackgroundColor(ContextCompat.getColor(context,R.color.frozenPiece));
            if(frozenPieces.isWhiteFrozen() &&
                    position == ((frozenPieces.getFrozenWhite().getRow()-1)*6)+frozenPieces.getFrozenWhite().getColumn()-1)
                v.setBackgroundColor(ContextCompat.getColor(context,R.color.frozenPiece));

            //special cells
            if (position == 0 || position == 3 || position == 32 || position == 35)
                v.setBackgroundColor(ContextCompat.getColor(context,R.color.specialCell));

            //load images
            Piece p = board.getPieceAtPosition(new Position(position / board.getSize() + 1, position % board.getSize() + 1));
            if (p != null) {
                int imageResource = context.getResources().getIdentifier(p.toString()+"_ic", "drawable", context.getPackageName());
                Drawable res = ContextCompat.getDrawable(context,imageResource);
                v.setImageDrawable(res);
                int border= ViewUtils.dipToPixels(context,6);
                v.setPadding(border,border,border,border);

            }
        } else {
            v = (ImageView) convertView;
        }
        return v;
    }
}