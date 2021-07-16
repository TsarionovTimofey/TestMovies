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
import com.example.testmovies.pojo.review.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemType> reviews;
    private OnFooterClickListener onFooterClickListener;

    public ReviewsAdapter() {
        this.reviews = new ArrayList<>();
    }

    public void setReviews(List<ItemType> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void clear() {
        reviews.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemType.TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
            return new ReviewViewHolder(view);
        } else if (viewType == ItemType.TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
            return new FooterViewHolder(view);
        } else
            throw new RuntimeException("there is no type that matches the type "
                    + viewType + " + make sure your using types correctly");
    }


    public interface OnFooterClickListener {
        void onFooterClick(int position);
    }

    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener) {
        this.onFooterClickListener = onFooterClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ReviewViewHolder) {
            Review review = (Review) reviews.get(position);
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;

            //Image
            if (review.getMultimedia() != null) {
                Picasso.get()
                        .load(review.getMultimedia().getSrc())
                        .into(reviewViewHolder.imageViewPoster);
            } else {
                Picasso.get()
                        .load(R.drawable.sample_review)
                        .into(reviewViewHolder.imageViewPoster);
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
        return reviews.size();
    }

    @Override
    public int getItemViewType(int position) {
        return reviews.get(position).getItemType();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewPoster;
        private TextView textViewMovieName;
        private TextView textViewMovieDescription;
        private TextView textViewCriticName;
        private TextView textViewReviewPublicationDate;
        private TextView textViewReviewPublicationTime;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
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
