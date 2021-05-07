package sample;

import java.util.*;

public class Solver {

    //DataStructures
    // ArrayLists for forward paths
    private ArrayList<ArrayList<Integer>> forwardPaths;
    private ArrayList<Double> pathsGains;
    private ArrayList<boolean[]> pathsFlags;
    // ArrayLists for loops
    private ArrayList<ArrayList<Integer>> loops;
    private ArrayList<Double> loopsGains;
    private ArrayList<boolean[]> loopsFlags;
    // ArrayLists for non-touching loops
    private ArrayList<Integer[]> nonTouchingLoops;
    private ArrayList<Double> nonTouchingLoopGains;


    private int numOfNodes;
    private double[][] graph;




    public Solver(double[][] graph) {
        this.graph = graph;
        forwardPaths = new ArrayList<>();
        pathsGains = new ArrayList<>();
        pathsFlags = new ArrayList<>();
        numOfNodes = graph.length;
        loops = new ArrayList<>();
        loopsGains = new ArrayList<>();
        loopsFlags = new ArrayList<>();
        nonTouchingLoops = new ArrayList<>();
        nonTouchingLoopGains = new ArrayList<>();
    }


    private void generatePathsAndLoops(ArrayList<Integer> path, boolean[] visited, int pointer) {
        path.add(pointer);
        visited[pointer] = true;
        // forward path case
        if (path.size() > 1 && pointer == numOfNodes - 1) {
            addForwardPath(new ArrayList<Integer>(path));
            return;
        }
        for (int i = 0; i < numOfNodes; i++) {
            if (graph[pointer][i] != 0) {
                if (!visited[i]) {
                    generatePathsAndLoops(path, visited, i);
                    path.remove(path.size() - 1);
                    visited[i] = false;
                    // loop case
                } else {
                    int index = path.indexOf(i);
                    if (index != -1) {
                        List<Integer> temp = path.subList(index, path.size());
                        addToLoops(new ArrayList<Integer>(temp));
                    }
                }
            }
        }
    }

    private void addForwardPath (ArrayList<Integer> addedPath) {
        forwardPaths.add(addedPath);
        pathsFlags.add(mapNodes(addedPath));
        pathsGains.add(calcGain(addedPath));
    }

    private double calcGain(ArrayList<Integer> arr) {
        double temp = 1;
        if (arr.size() > 1) {
            for (int i = 0; i < arr.size() - 1; i++)
                temp *= graph[arr.get(i)][arr.get(i + 1)];
            return temp;
        }
        return graph[arr.get(0)][arr.get(0)];

    }

    private boolean[] mapNodes(ArrayList<Integer> arr) {
        boolean[] temp = new boolean[numOfNodes];
        for (int i = 0; i < arr.size(); i++) {
            temp[arr.get(i)] = true;
        }
        return temp;
    }

    private void addToLoops(ArrayList<Integer> arr) {
        arr.add(arr.get(0));
        if (!checkLoop(arr)) {
            loops.add(arr);
            loopsFlags.add(mapNodes(arr));
            loopsGains.add(calcGain(arr));
        }
    }

    private boolean checkLoop(ArrayList<Integer> a) {
        boolean[] loop = mapNodes(a);
        for (int i = 0; i < loops.size(); i++) {
            if (loops.get(i).size() == a.size() && isEquivalentLoop(loop, loopsFlags.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isEquivalentLoop(boolean[] arr1, boolean[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i])
                return false;
        }
        return true;
    }

    private void generateNonTouching(ArrayList<ArrayList<Integer>> arrList,
                                     int count) {
        Set<List<Integer>> storedLoops = new HashSet<>();
        boolean moveOnFlag = false;
        ArrayList<ArrayList<Integer>> nextArrList = new ArrayList<>();
        for (int i = 0; i < arrList.size(); i++) {
            for (int j = i + 1; j < arrList.size(); j++) {
                for (int k = 0; k < arrList.get(j).size(); k++) {
                    int cand = arrList.get(j).get(k);
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.addAll(arrList.get(i));
                    temp.add(cand);
                    if (isNonTouching(temp)) {
                        Collections.sort(temp);
                        if (!storedLoops.contains(temp)) {
                            storedLoops.add(temp);
                            moveOnFlag = true;
                            nextArrList.add(new ArrayList<>());
                            nextArrList.get(nextArrList.size() - 1)
                                    .addAll(temp);
                            nonTouchingLoops.add(temp.toArray(new Integer[temp.size()]));
                            nonTouchingLoopGains.add(calcNonTouchingGain(temp));
                        }
                    }
                }
            }

        }
        if (moveOnFlag) {
            count++;
            generateNonTouching(nextArrList, count);
        }
    }

    private boolean isNonTouching(ArrayList<Integer> arr) {
        int flag;
        // looping over columns
        for (int i = 0; i < numOfNodes; i++) {
            flag = 0;
            // looping over rows
            for (int j = 0; j < arr.size(); j++) {
                if (loopsFlags.get(arr.get(j))[i])
                    flag++;
            }
            if (flag > 1)
                return false;
        }
        return true;
    }

    private boolean isNonTouchingWithPath(ArrayList<Integer> arr, int fbNum) {
        int flag;
        // looping over columns
        for (int i = 0; i < numOfNodes; i++) {
            flag = 0;
            // looping over rows
            for (int j = 0; j < arr.size(); j++) {
                if (loopsFlags.get(arr.get(j))[i])
                    flag++;
            }
            if (pathsFlags.get(fbNum)[i])
                flag++;
            if (flag > 1)
                return false;
        }
        return true;
    }

    private double calcNonTouchingGain(ArrayList<Integer> arr) {
        double gain = 1;
        for (int j = 0; j < arr.size(); j++)
            gain *= loopsGains.get(arr.get(j));
        return gain;
    }

    public double solve() {
        generatePathsAndLoops(new ArrayList<>(), new boolean[numOfNodes], 0);
        ArrayList<ArrayList<Integer>> loopLabels = new ArrayList<>();
        for (int i = 0; i < loops.size(); i++) {
            loopLabels.add(new ArrayList<>());
            loopLabels.get(loopLabels.size() - 1).add(i);
        }
        generateNonTouching(loopLabels, 1);
        double curr = 0;
        double delta = 0;
        int e = -1;
        int nth = 1;
        for (int i = 0; i < nonTouchingLoops.size(); i++) {
            if (nonTouchingLoops.get(i).length == nth) {
                curr += nonTouchingLoopGains.get(i);
            } else {
                delta += e * curr;
                e *= -1;
                ++nth;
            }

        }
        delta = 1 - delta;

        double numerator = 0;
        double deltaN;

        for (int i = 0; i < forwardPaths.size(); i++) {
            deltaN = 1;
            curr = 0;
            e = -1;
            for (int j = 0; j < nonTouchingLoops.size(); j++) {
                if (isNonTouchingWithPath(new ArrayList<Integer>(Arrays.asList(nonTouchingLoops.get(j))), i)) {
                    curr += e * nonTouchingLoopGains.get(j);
                    e *= -1;
                } else
                    break;
            }
            deltaN += curr;
            numerator += deltaN * pathsGains.get(i);
        }
        return numerator / delta;
    }

    //Getters
    public ArrayList<String> getLoops() {
        ArrayList<String> loopsString = new ArrayList<>();
        for (ArrayList<Integer> arr : loops) {
            for (int i = 0; i < arr.size(); i++) {
                loopsString.add((arr.get(i) + 1) + " ");
            }
        }
        return loopsString;
    }

    public ArrayList<String> getNonTouchingLoops() {
        ArrayList<String> temp = getLoops();
        ArrayList<String> nonTouchingString = new ArrayList<>();
        for (Integer[] arr : nonTouchingLoops) {
            if (arr.length > 0)
                nonTouchingString.add(temp.get(arr[0]));
            for (int i = 1; i < arr.length; i++)
                nonTouchingString.add( " , " + temp.get(arr[i]));
        }
        return nonTouchingString;
    }

    public ArrayList<String> getForwardPaths() {
        ArrayList<String> pathsString = new ArrayList<>();
        for (ArrayList<Integer> arr : forwardPaths) {
            for (int i = 0; i < arr.size(); i++) {
                pathsString.add((arr.get(i) + 1) + " ");
            }
        }
        return pathsString;
    }

    public ArrayList<Double> getPathsGains() {
        return pathsGains;
    }

    public ArrayList<Double> getLoopGains() {
        return loopsGains;
    }

    public ArrayList<Double> getNonTouchingGains() {
        return nonTouchingLoopGains;
    }
    public static void main(String [] args){
        double [][]arr = new double[5][5];
        for (int  i = 0 ; i < 5 ; i++){
            for (int  j = 0 ; j<5;j++){
                arr[i][j]=i+j;
            }
        }
        Solver solver = new Solver(arr);
        solver.solve();
        System.out.println(solver.getLoopGains().size());
        System.out.println(solver.getForwardPaths().size());

    }
}