package com.example.testmovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmovies.R;
import com.example.testmovies.adapters.items.ItemType;
import com.example.testmovies.pojo.critic.Critic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CriticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemType> critics;
    private OnCriticClickListener onCriticClickListener;
    private OnFooterClickListener onFooterClickListener;


    public CriticsAdapter() {
        this.critics = new ArrayList<>();
    }

    public void setCritics(List<ItemType> critics) {
        this.critics = critics;
        notifyDataSetChanged();
    }

    public void clear() {
        critics.clear();
        notifyDataSetChanged();
    }

    public interface OnCriticClickListener {
        void onCriticClick(ItemType critic);
    }

    public void setOnCriticClickListener(OnCriticClickListener onCriticClickListener) {
        this.onCriticClickListener = onCriticClickListener;
    }

    public interface OnFooterClickListener {
        void onFooterClick(int position);
    }

    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener) {
        this.onFooterClickListener = onFooterClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemType.TYPE_MUTABLE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.critic_item, parent, false);
            return new CriticsViewHolder(view);
        } else if (viewType == ItemType.TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
            return new FooterViewHolder(view);
        } else
            throw new RuntimeException("there is no type that matches the type "
                    + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CriticsViewHolder) {
            Critic critic = (Critic) critics.get(position);
            CriticsViewHolder criticsViewHolder = (CriticsViewHolder) holder;

            //Image
            if (critic.getMultimedia() != null) {
                Picasso.get()
                        .load(critic.getMultimedia().getResource().getSrc())
                        .error(R.drawable.sample_critic)
                        .into(criticsViewHolder.imageViewCriticPoster);
            } else {
                Picasso.get()
                        .load(R.drawable.sample_critic)
                        .into(criticsViewHolder.imageViewCriticPoster);
            }

            //Text
            criticsViewHolder.textViewCriticNameInCritics.setText(critic.getDisplayName());

        } else if (holder instanceof FooterViewHolder) {

        }

    }

    @Override
    public int getItemViewType(int position) {
        return critics.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return critics.size();
    }

    class CriticsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewCriticPoster;
        private TextView textViewCriticNameInCritics;

        public CriticsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCriticPoster = itemView.findViewById(R.id.imageViewCriticPosterCriticItem);
            textViewCriticNameInCritics = itemView.findViewById(R.id.textViewCriticNameCriticItem);
            itemView.setOnClickListener(v -> {
                if (onCriticClickListener != null) {
                    onCriticClickListener.onCriticClick(critics.get(getAdapterPosition()));
                }
            });
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (onFooterClickListener != null) {
                    onFooterClickListener.onFooterClick(getAdapterPosition());
                }
            });
        }
    }

}
