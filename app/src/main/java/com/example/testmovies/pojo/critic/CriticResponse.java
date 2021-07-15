package com.example.testmovies.pojo.critic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CriticResponse {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("copyright")
        @Expose
        private String copyright;
        @SerializedName("num_results")
        @Expose
        private int numResults;
        @SerializedName("results")
        @Expose
        private List<Critic> critics = null;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public int getNumResults() {
            return numResults;
        }

        public void setNumResults(int numResults) {
            this.numResults = numResults;
        }

        public List<Critic> getCritics() {
            return critics;
        }

        public void setCritics(List<Critic> critics) {
            this.critics = critics;
        }
}
