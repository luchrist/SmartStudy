package com.example.smartstudy;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MathFragment extends Fragment {


    TextView calc, hs, score;
    EditText sol;
    Button send;
    CheckBox add, sub, mul, div;
    boolean start;
    String op;
    int numOne;
    int numTwo;
    int points, highestPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_math, container, false);
        start = true;
        points = 0;
        SharedPreferences sp = getActivity().getSharedPreferences("SP", 0);
        highestPoints = sp.getInt("highscore", 0);
        calc = view.findViewById(R.id.word);
        hs = view.findViewById(R.id.mathHighscore);
        score = view.findViewById(R.id.mathScore);
        sol = view.findViewById(R.id.trans);
        send = view.findViewById(R.id.sendBtn);
        add = view.findViewById(R.id.checkBoxAdd);
        sub = view.findViewById(R.id.checkBoxSub);
        mul = view.findViewById(R.id.checkBoxMul);
        div = view.findViewById(R.id.checkBoxDiv);

        hs.setText("" + highestPoints);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start == true){
                    start = false;
                    op = getOperator();  // Division soll nur auf 0,5 genau sein.
                    numOne = getNumber(0);

                    numTwo = getNumber(0);
                    if (op.equals("/")){
                        while(numTwo == 0){
                            numTwo = getNumber(0);
                            System.out.println("Stuck!!!!!");
                        }
                    }
                    send.setText("Send");
                    calc.setText(numOne + " " + op +" "+ numTwo);

                }else{
                    checkSol();
                    score.setText("" + points);

                    if(points > highestPoints){
                        highestPoints = points;
                        hs.setText(""+highestPoints);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("highscore", highestPoints );
                        editor.commit();
                    }

                    op = getOperator();  // Division soll nur auf 0,5 genau sein.

                    numOne = getNumber((int)((points + 5)/10));

                    numTwo = getNumber((int)(points /10));
                    if (op.equals("/")){
                        while(numTwo == 0){
                            numTwo = getNumber((int)(points /10));
                            System.out.println("Stuck!!!!!");
                        }
                    }
                    calc.setText(numOne + " " + op +" "+ numTwo);

                }

            }
        });

        return view;
    }

    private void checkSol() {
        double solution;
        String userSolString = sol.getText().toString();
        double userSol = Double.parseDouble(userSolString);
        if (op.equals("+")){
            solution = numOne + numTwo;
            if(userSol == solution){
                points ++;

                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
            }else{
                points = 0;

                Toast.makeText(getActivity(), "Wrong! correct answer is: " + solution, Toast.LENGTH_SHORT).show();
            }
        }else if (op.equals("-")){
            solution = numOne - numTwo;
            if(userSol == solution){
                points ++;

                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
            }else{
                points = 0;

                Toast.makeText(getActivity(), "Wrong! correct answer is: " + solution, Toast.LENGTH_SHORT).show();
            }
        }else if (op.equals("*")){
            solution = numOne * numTwo;
            if(userSol == solution){
                points ++;

                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
            }else{
                points = 0;

                Toast.makeText(getActivity(), "Wrong! correct answer is: " + solution, Toast.LENGTH_SHORT).show();
            }
        }else if (op.equals("/")){
            solution = Double.valueOf(numOne) / Double.valueOf(numTwo);
            userSol = Math.round((userSol*10)/10.0);
            double solutionRound = Math.round((solution*10)/10.0);
            if(userSol == solutionRound){
                points ++;

                Toast.makeText(getActivity(), "Correct! corret answer is: " + solution, Toast.LENGTH_SHORT).show();
            }else{
                points = 0;

                Toast.makeText(getActivity(), "Wrong! correct answer is: " + solution + " Rundet: " + solutionRound, Toast.LENGTH_SHORT).show();

            }
        }



    }

    private int getNumber(int difficulty) {
        Random rand = new Random();
        return rand.nextInt((difficulty +1) * 10 +1);
    }

    private String getOperator() {
        ArrayList<String> operators = new ArrayList<String>();
        if(add.isChecked()){
            operators.add("+");
        }
        if (sub.isChecked()){
            operators.add("-");
        }
        if (mul.isChecked()){
            operators.add("*");
        }
        if(div.isChecked()){
            operators.add("/");
        }
        Random rand = new Random();
// Obtain a number between [0 - size-1].
        try {
            int n = rand.nextInt(operators.size());

            return operators.get(n);
        }catch (IllegalArgumentException exception){
            Toast.makeText(getActivity(), "NO OPERATOR IS CHECKED !!!", Toast.LENGTH_SHORT).show();
        }
        return "+";
    }
}