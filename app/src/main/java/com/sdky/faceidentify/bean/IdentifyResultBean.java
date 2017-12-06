package com.sdky.faceidentify.bean;

import java.util.List;

/**
 * Created by liurui on 2017/4/24.
 */

public class IdentifyResultBean {

    /**
     * result_num : 1
     * results : [{"uid":"test_user_001","user_info":"userInfo001","scores":[100]}]
     * log_id : 2959557585
     */

    private int result_num;
    private long log_id;
    private List<ResultsBean> results;

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * uid : test_user_001
         * user_info : userInfo001
         * scores : [100]
         */

        private String uid;
        private String user_info;
        private List<Integer> scores;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUser_info() {
            return user_info;
        }

        public void setUser_info(String user_info) {
            this.user_info = user_info;
        }

        public List<Integer> getScores() {
            return scores;
        }

        public void setScores(List<Integer> scores) {
            this.scores = scores;
        }
    }
}
