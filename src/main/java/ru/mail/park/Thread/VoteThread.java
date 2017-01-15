package ru.mail.park.Thread;

/**
 * Created by sergeigavrilko on 14.11.16.
 */
public class VoteThread {

        private Integer vote;
        private Integer thread;

        public VoteThread(Integer vote, Integer thread) {
            this.vote = vote;
            this.thread = thread;
        }

        public VoteThread() {
        }

        public Integer getVote() {
            return vote;
        }

        public Integer getThread() {
            return thread;
        }

        public void setVote(Integer vote) {
            this.vote = vote;
        }

        public void setthread(Integer thread) {
            this.thread = thread;
        }
}
