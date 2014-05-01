package es.us.jegalan.cplex.testing;

// ---------------------------------------------------------------*- Java -*-
// File: ./examples/src/java/Sports.java
// --------------------------------------------------------------------------
// Licensed Materials - Property of IBM
//
// 5724-Y48 5724-Y49 5724-Y54 5724-Y55 5725-A06 5725-A29
// Copyright IBM Corporation 1990, 2013. All Rights Reserved.
//
// Note to U.S. Government Users Restricted Rights:
// Use, duplication or disclosure restricted by GSA ADP Schedule
// Contract with IBM Corp.
// --------------------------------------------------------------------------

/* ------------------------------------------------------------

Problem Description
-------------------

The problem involves finding a schedule for a sports league. The league has 10 
teams that play games over a season of 18 weeks. Each team has a home arena and 
plays each other team twice during the season, once in its home arena and once in 
the opposing team's home arena. For each of these games, the team playing at its 
home arena is referred to as the home team; the team playing at the opponent's 
arena is called the away team. There are 90 games altogether.

Each of the 18 weeks in the season has five identical slots to which games can be 
assigned. Each team plays once a week. For each pair of teams, these two teams are 
opponents twice in a season; these two games must be scheduled in different halves 
of the season. Moreover, these two games must be scheduled at least six weeks 
apart. A team must play at home either the first or last week but not both.

A break is a sequence of consecutive weeks in which a team plays its games either 
all at home or all away. No team can have a break of three or more weeks in it. The
objective in this problem is to minimize the total number of breaks the teams play. 

------------------------------------------------------------ */

import ilog.cp.*;
import ilog.concert.*;


public class Sports {
    public static int Game(int h, int a, int n) {
        if (a>h)
            return h * (n - 1) + a - 1;
        else
            return h * (n - 1) + a;
    }
    public static int min(int a, int b) {
        return (a>=b? b : a);
    }
    public static void main(String[] args) {
        try {   
            int n = 10;
            if (args.length > 0)
                n = Integer.parseInt(args[0]);
            if ((n % 2) == 1)
                n++;
            System.out.println("Finding schedule for " + n + " teams");
            int nbWeeks = 2 * (n - 1);
            int nbGamesPerWeek = n / 2;
            int nbGames = n * (n - 1);
            IloCP cp = new IloCP();

            IloIntVar[][] games = new IloIntVar[nbWeeks][];
            IloIntVar[][] home = new IloIntVar[nbWeeks][];
            IloIntVar[][] away = new IloIntVar[nbWeeks][];
 
            for (int i = 0; i < nbWeeks; i++) {
                home[i]  = cp.intVarArray(nbGamesPerWeek, 0, n - 1);
                away[i]  = cp.intVarArray(nbGamesPerWeek, 0, n - 1);
                games[i] = cp.intVarArray(nbGamesPerWeek, 0, nbGames - 1);
            }
            //
            // For each play slot, set up correspondance between game id,
            // home team, and away team
            // 

            IloIntTupleSet gha = cp.intTable(3);
            int[] tuple = new int[3];
            for (int i = 0; i < n; i++) {
                tuple[0] = i;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        tuple[1] = j;
                        tuple[2] = Game(i, j, n);
                        cp.addTuple(gha, tuple);
                    }
                }
            }

            for (int i = 0; i < nbWeeks; i++) {
                for (int j = 0; j < nbGamesPerWeek; j++) {
                    IloIntVar[] vars=cp.intVarArray(3);
                    vars[0]= home[i][j];
                    vars[1]= away[i][j];
                    vars[2]= games[i][j];
                    cp.add(cp.allowedAssignments(vars, gha));
                }
            }
            //
            // All teams play each week
            //
            for (int i = 0; i < nbWeeks; i++) {
                IloIntVar[] teamsThisWeek = cp.intVarArray(n);
                for (int j=0; j < nbGamesPerWeek; j++) {
                    teamsThisWeek[j]= home[i][j];
                    teamsThisWeek[nbGamesPerWeek+j]=away[i][j];
                }
                cp.add(cp.allDiff(teamsThisWeek));
            }
            //
            // Dual representation: for each game id, the play slot is maintained
            // 
            IloIntVar[] weekOfGame= cp.intVarArray(nbGames, 0, nbWeeks - 1);
            IloIntVar[] allGames= cp.intVarArray(nbGames);
            IloIntVar[] allSlots= cp.intVarArray(nbGames, 0, nbGames - 1);
            for (int i = 0; i < nbWeeks; i++)
                for (int j = 0; j < nbGamesPerWeek; j++)
                    allGames[i*nbGamesPerWeek+j]=games[i][j];
            cp.add(cp.inverse(allGames, allSlots));
            for (int i = 0; i < nbGames; i++)
                cp.add(cp.eq(weekOfGame[i], cp.div(allSlots[i], nbGamesPerWeek)));
            //
            // Two half schedules.  Cannot play the same pair twice in the same half.
            // Plus, impose a minimum number of weeks between two games involving
            // the same teams (up to six weeks)
            //
            int mid = nbWeeks / 2;
            int overlap = 0;
            if (n >= 6)
                overlap = min(n / 2, 6);
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int g1 = Game(i, j, n);
                    int g2 = Game(j, i, n); 
                    cp.add(cp.equiv(cp.ge(weekOfGame[g1], mid) , cp.lt(weekOfGame[g2], mid)));
                    // Six week difference...
                    if (overlap != 0)
                        cp.add(cp.ge(cp.abs( cp.diff(weekOfGame[g1], weekOfGame[g2])), overlap));
                }
            }

            //
            // Can't have three homes or three aways in a row.
            //
            IloIntVar[][] playHome = new IloIntVar[n][];
            for (int i = 0; i < n; i++) {
                playHome[i] = cp.intVarArray(nbWeeks, 0, 1);
                for (int j = 0; j < nbWeeks; j++)
                    cp.add(cp.eq(playHome[i][j], cp.count(home[j], i)));
                for (int j = 0; j < nbWeeks -3; j++) {
                    IloIntVar[] window = cp.intVarArray(3);
                    for (int k = j; k < j + 3; k++)
                        window[k-j]=playHome[i][k];
                    IloIntExpr windowSum = cp.sum(window);
                    cp.add(cp.ge(windowSum, 1));
                    cp.add(cp.le(windowSum, 2));
                }
            }

            //
            // If we start the season home, we finish away and vice versa.
            //
            for (int i = 0; i < n; i++)
                cp.add(cp.neq(playHome[i][0], playHome[i][nbWeeks-1]));

            //
            // Objective: minimize the number of `breaks'.  A break is
            //            two consecutive home or away matches for a
            //            particular team
            IloIntVar[] teamBreaks= cp.intVarArray(n, 0, nbWeeks / 2);
            for (int i = 0; i < n; i++) {
                IloIntExpr nbreaks= cp.constant(0);
                for (int j = 1; j < nbWeeks; j++)
                    nbreaks = cp.sum(nbreaks, 
                                     cp.intExpr(cp.eq(playHome[i][j-1],
                                                      playHome[i][j])));
                cp.add(cp.eq(teamBreaks[i], nbreaks));
            }
            IloIntVar breaks = cp.intVar(n - 2, n * (nbWeeks / 2));
            cp.add(cp.eq(breaks, cp.sum(teamBreaks)));
            cp.add(cp.minimize(breaks));

            //
            // Redundant constraints
            //
            
            // Each team plays home the same number of times as away
            for (int i = 0; i < n; i++)
                cp.add(cp.eq(cp.sum(playHome[i]),nbWeeks / 2));

            // Breaks must be even for each team
            for (int i = 0; i < n; i++)
                cp.add(cp.eq(cp.modulo(teamBreaks[i], 2),0));

            //    
            // Symmetry breaking constraints
            // 
            
            // Teams are interchangeable.  Fix first week.
            // Also breaks reflection symmetry of the whole schedule.
            for (int i = 0; i < nbGamesPerWeek; i++) {
                cp.add(cp.eq(home[0][i], i * 2));
                cp.add(cp.eq(away[0][i], i * 2 + 1));
            }

            // Order of games in each week is arbitrary.
            // Break symmetry by forcing an order.
            for (int i = 0; i < nbWeeks; i++)
                for (int j = 1; j < nbGamesPerWeek; j++)
                    cp.add(cp.gt(games[i][j], games[i][j-1]));

            cp.setParameter(IloCP.DoubleParam.TimeLimit, 60); 
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Normal);
            IloVarSelector varSel = cp.selectSmallest(cp.varIndex(allGames));;
            IloValueSelector valSel = cp.selectRandomValue();

            IloSearchPhase phase = cp.searchPhase(allGames,
                                                  cp.intVarChooser(varSel),
                                                  cp.intValueChooser(valSel));
            cp.startNewSearch(phase);
            while (cp.next()) {
                System.out.println("Solution at " + (int)cp.getValue(breaks));
                for (int j = 0; j < nbWeeks; j++) {
                    System.out.print("Week " + j+ ": ");
                    if ( j < 10 ) System.out.print(" ");
                    for (int i = 0; i < nbGamesPerWeek; i++) {
                        int h = (int) cp.getValue(home[j][i]);
                        int a = (int) cp.getValue(away[j][i]);
                        if (h >= 10) System.out.print(h);
                        else System.out.print(" " + h);
                        System.out.print("-");
                        if (a >= 10)  System.out.print(a);
                        else          System.out.print(a + " ");
                        System.out.print(" ");
                    }
                    System.out.println();
                }
                System.out.println( "Team schedules");
                for (int i = 0; i < n; i++) {
                    System.out.print("T "+ i + ":\t");
                    int prev = -1;
                    int brks = 0;
                    for (int j = 0; j < nbWeeks; j++) {
                        for (int k = 0; k < nbGamesPerWeek; k++) {
                            if (cp.getValue(home[j][k]) == i) {
                                int t = (int) cp.getValue(away[j][k]);
                                if (t >= 10)System.out.print(t + "H ");
                                else         System.out.print(" " + t + "H ");
                                if (prev == 0) brks ++ ;
                                prev = 0;
                            }
                            if (cp.getValue(away[j][k]) == i) {
                                int t = (int) cp.getValue(home[j][k]);
                                if (t >= 10) System.out.print(t + "A ");
                                else          System.out.print(" " + t + "A ");
                                if(prev == 1) brks ++;
                                prev = 1;
                            }
                        }
                    }
                    System.out.println("   " + brks + " breaks");
                }
            }
            cp.endSearch();
        } catch (IloException e) {
            System.err.println("Error " + e);
        }
    }
}
