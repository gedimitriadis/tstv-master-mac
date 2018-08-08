package com.partner.android.valauroPartner;

public class pairOfRingsItem implements java.io.Serializable{




        /** ResourceID for woman's ring */
        private int mRingImageWoman;
         /** ResourceID for man's ring */
        private int mRingImageMan;
        /** Design code woman */
        private String mDesignWoman;
        /** Design code man */
        private String mDesignMan;
        /** carats for woman ring */
        private String mCaratsWoman;
        /** carats for man ring */
        private String mCaratsMan;
        /** profile for woman ring */
        private String mProfileWoman;
        /** profile for man ring */
        private String mProfileMan;
         /** number of stones for woman ring */
        private int mStonesWoman;
         /** number of stones for man ring */
        private int mStonesMan;
        private String mPairCost;




        /**
         * Create a new pairOfRingsItem object
         * @param image is the image which will be shown as the book's image
         * @param title is the book's title
         * @param author is the book's author
         * @param language is the book's language
         * @param description is a short description of the book
         * @param url is the link to the book's previewURL page
         */

        public pairOfRingsItem(int ringImageWoman, int ringImageMan, String designWoman, String designMan, String caratsWoman, String caratsMan, String profileWoman, String profileMan, int stonesWoman, int stonesMan, String pairCost) {
            mRingImageWoman = ringImageWoman;
            mRingImageMan = ringImageMan;
            mDesignWoman = designWoman;
            mDesignMan = designMan;
            mCaratsWoman = caratsWoman;
            mCaratsMan=caratsMan;
            mProfileWoman = profileWoman;
            mProfileMan = profileMan;
            mStonesWoman = stonesWoman;
            mStonesMan = stonesMan;
            mPairCost = pairCost;
        }

        /** getter methods */
        public int getRingImageWoman(){
            return mRingImageWoman;
        }

        public int getRingImageMan(){
            return mRingImageMan;
        }


        public String getDesignWoman(){
            return mDesignWoman;
        }

        public String getDesignMan(){
            return mDesignMan;
        }

        public String getCaratsWoman(){
            return mCaratsWoman;
        }

        public String getCaratsMan(){
            return mCaratsMan;
        }

        public String getProfileWoman(){
            return mProfileWoman;
        }

        public String getProfileMan(){
            return mProfileMan;
        }

        public int getStonesWoman(){
            return mStonesWoman;
        }

        public int getStonesMan(){
            return mStonesMan;
        }

        public String getPairCost(){
            return mPairCost;
        }

}
