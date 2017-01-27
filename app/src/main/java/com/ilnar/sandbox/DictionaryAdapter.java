package com.ilnar.sandbox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilnar.sandbox.dictionary.DictionaryRecord;

import java.util.List;

/**
 * Created by ilnar on 29.05.16.
 */
public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.MyViewHolder> {
    private List<DictionaryRecord> recordsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView word, translation;

        public MyViewHolder(View v) {
            super(v);
            word = (TextView) v.findViewById(R.id.word);
            translation = (TextView) v.findViewById(R.id.translation);
        }
    }

    public DictionaryAdapter(List<DictionaryRecord> recordsList) {
        this.recordsList = recordsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionary_record_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DictionaryRecord dictionaryRecord = recordsList.get(position);
        holder.word.setText(dictionaryRecord.getWord());
        holder.translation.setText(dictionaryRecord.getTranslation()[0]);
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }


}
