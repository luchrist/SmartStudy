package de.christcoding.smartstudy.utilities;

import de.christcoding.smartstudy.R;

public class StudyUtilities {

    public static int getRecommendation(double grade) {
        if(grade <= 1.5) {
            return R.string.excellent_get_ready_to_get_called_a_nerd;
        } else if (grade <= 2.5) {
            return R.string.good_job_you_are_ready_for_the_test;
        } else if (grade <= 3.5) {
            return R.string.ok_but_you_can_do_better;
        } else if (grade <= 4.5) {
            return R.string.not_eneugh_you_need_to_study_more_or_start_praying;
        } else {
            return R.string.horrible_grade_consider_staying_at_home_for_that_test;
        }
    }

    public static double getGrade(double ratio) {
        if (ratio >= 0.95){
            return 1;
        } else if (ratio >= 0.85){
            return 1.5;
        } else if (ratio >= 0.75){
            return 2;
        } else if (ratio >= 0.65){
            return 2.5;
        } else if (ratio >= 0.55){
            return 3;
        } else if (ratio >= 0.45){
            return 3.5;
        } else if (ratio >= 0.35){
            return 4;
        } else if (ratio >= 0.25){
            return 4.5;
        } else if (ratio >= 0.15){
            return 5;
        } else {
            return 6;
        }
    }
}
