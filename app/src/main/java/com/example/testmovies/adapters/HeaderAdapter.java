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
import com.example.testmovies.pojo.review.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemType> items;
    private OnFooterClickListener onFooterClickListener;

    public HeaderAdapter() {
        items = new ArrayList<>();
    }

    public void setItems(List<ItemType> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.critic_header, parent, false);
            return new ViewHolderCriticHeader(view);

        } else if (viewType == ItemType.TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
            return new ReviewViewHolder(view);

        } else if (viewType == ItemType.TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
            return new FooterViewHolder(view);

        } else
            throw new RuntimeException("there is no type that matches the type "
                    + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Head Critic Holder
        if (holder instanceof ViewHolderCriticHeader) {
            Critic critic = (Critic) items.get(position);
            ViewHolderCriticHeader viewHolderCriticHeader = (ViewHolderCriticHeader) holder;

            //Text
            viewHolderCriticHeader.textViewCriticNameCriticHeader.setText(critic.getDisplayName());
            if (critic.getBio() != null) {
                viewHolderCriticHeader.textViewCriticBioCriticHeader.setText(critic.getBio().replace("<br/><br/>", ""));
            } else {
                viewHolderCriticHeader.textViewCriticBioCriticHeader.setText("");
            }
            if (critic.getStatus().equals("")) {
                viewHolderCriticHeader.textViewCriticStatusCriticHeader.setText(R.string.not_available_label);
            } else {
                viewHolderCriticHeader.textViewCriticStatusCriticHeader.setText(critic.getStatus());
            }

            //Image
            if (critic.getMultimedia() != null) {
                Picasso.get()
                        .load(critic.getMultimedia().getResource().getSrc())
                        .error(R.drawable.sample_critic)
                        .into(viewHolderCriticHeader.imageViewCriticPosterCriticHeader);
            } else {
                Picasso.get()
                        .load(R.drawable.sample_critic)
                        .into(viewHolderCriticHeader.imageViewCriticPosterCriticHeader);
            }

            //Item Review Holder
        } else if (holder instanceof ReviewViewHolder) {
            Review review = (Review) items.get(position);
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;

            //Image
            if (review.getMultimedia() != null) {
                Picasso.get()
                        .load(review.getMultimedia().getSrc())
                        .into(reviewViewHolder.imageViewPosterReview);
            } else {
                Picasso.get()
                        .load(R.drawable.sample_review)
                        .into(reviewViewHolder.imageViewPosterReview);
            }

            //Text
            reviewViewHolder.textViewMovieName.setText(review.getDisplayTitle());
            reviewViewHolder.textViewCriticName.setText(review.getByline());
            reviewViewHolder.textViewReviewPublicationDate.setText(review.getPublicationDate().replace('-', '/'));

            //Description
            String description = review.getSummaryShort();
            if (description.equals("")) {
                reviewViewHolder.textViewMovieDescription.setText(R.string.movie_description_is_not_available_label);
            } else {
                if (description.length() >= 150) {
                    description = description.substring(0, 150) + "...";
                }
                reviewViewHolder.textViewMovieDescription.setText(description);
            }

            //Publication time
            //Since the api does not provide getting the publication date,
            // we will parse the date update
            if (review.getDateUpdated() != null) {
                String publicationTime = review.getDateUpdated().substring(11);
                reviewViewHolder.textViewReviewPublicationTime.setText(publicationTime);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
    }

    class ViewHolderCriticHeader extends RecyclerView.ViewHolder {

        private ImageView imageViewCriticPosterCriticHeader;
        private TextView textViewCriticNameCriticHeader;
        private TextView textViewCriticBioCriticHeader;
        private TextView textViewCriticStatusCriticHeader;

        public ViewHolderCriticHeader(@NonNull View itemView) {
            super(itemView);
            imageViewCriticPosterCriticHeader = itemView.findViewById(R.id.imageViewCriticPosterCriticHeader);
            textViewCriticNameCriticHeader = itemView.findViewById(R.id.textViewCriticNameCriticHeader);
            textViewCriticBioCriticHeader = itemView.findViewById(R.id.textViewCriticBioCriticHeader);
            textViewCriticStatusCriticHeader = itemView.findViewById(R.id.textViewCriticStatusCriticHeader);
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewPosterReview;
        private TextView textViewMovieName;
        private TextView textViewMovieDescription;
        private TextView textViewCriticName;
        private TextView textViewReviewPublicationDate;
        private TextView textViewReviewPublicationTime;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewPosterReview = itemView.findViewById(R.id.imageViewPoster);
            textViewMovieName = itemView.findViewById(R.id.textViewMovieName);
            textViewMovieDescription = itemView.findViewById(R.id.textViewMovieDescription);
            textViewCriticName = itemView.findViewById(R.id.textViewCriticName);
            textViewReviewPublicationDate = itemView.findViewById(R.id.textViewReviewDate);
            textViewReviewPublicationTime = itemView.findViewById(R.id.textViewReviewTime);
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
