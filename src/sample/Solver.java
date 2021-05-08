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

    private ArrayList<Double> deltas;

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
        deltas = new ArrayList<>();
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

    private boolean isNonTouchingLoopWithPath(int loopNum, int pathNum) {
        boolean flag = true;
        boolean[] path = pathsFlags.get(pathNum);
        boolean[] loop = loopsFlags.get(loopNum);
        for (int i = 0; i < numOfNodes; i++) {
            if (path[i] && loop[i]) {
                flag = false;
                break;
            }
        }
        return flag;
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

        double sum = 0;
        double delta = 0;
        double oneLoopSum = 0;
        for (int i = 0; i < loopsGains.size(); i++) {
            oneLoopSum += loopsGains.get(i);
        }
        double nonTouchingLoopsSum = 0;
        for (int i = 0; i < nonTouchingLoops.size(); i++) {
            if (nonTouchingLoops.get(i).length % 2  == 0) {
                nonTouchingLoopsSum += nonTouchingLoopGains.get(i);
            } else {
                nonTouchingLoopsSum += (-1) * nonTouchingLoopGains.get(i);
            }

        }

        delta = 1 - oneLoopSum + nonTouchingLoopsSum;

        double nominatorTerm = 0;
        double deltaN;

        for (int i = 0; i < forwardPaths.size(); i++) {
            sum = 0;
            for (int j = 0; j < loops.size(); j++) {
                if (isNonTouchingLoopWithPath(j, i)) {
                    sum += loopsGains.get(j);
                }
            }
            deltaN = 1 - sum;
            deltas.add(deltaN);
            nominatorTerm += deltaN * pathsGains.get(i);
        }
        deltas.add(delta);
        return nominatorTerm / delta;
    }

    //Getters
    public String[] getLoops() {
        String loopsString[] = new String[loops.size()];
        int count = 0;
        for (ArrayList<Integer> arr : loops) {
            loopsString[count] = "";
            for (int i = 0; i < arr.size(); i++) {
                loopsString[i] += (arr.get(i) + 1) + " ";
            }
            count++;
        }
        return loopsString;
    }

    public String[] getNonTouchingLoops() {
        String[] temp = getLoops();
        String nonString[] = new String[nonTouchingLoops.size()];
        int count = 0;
        for (Integer[] arr : nonTouchingLoops) {
            nonString[count] = "";

            if (arr.length > 0)
                nonString[count] += temp[arr[0]];

            for (int i = 1; i < arr.length; i++)
                nonString[i] += " , " + temp[arr[i]];

            count++;
        }
        return nonString;
    }

    public String[] getForwardPaths() {
        String pathsString[] = new String[forwardPaths.size()];
        int itr = 0;
        for (ArrayList<Integer> arr : forwardPaths) {
            pathsString[itr] = "";
            for (int i = 0; i < arr.size(); i++) {
                pathsString[itr] += (arr.get(i) + 1) + " ";
            }
            itr++;
        }
        return pathsString;
    }

    public Double[] getDeltas() {
        return deltas.toArray(new Double[deltas.size()]);
    }

    public Double[] getForwardPathGains() {
        return pathsGains.toArray(new Double[pathsGains.size()]);
    }

    public Double[] getLoopGains() {
        return loopsGains.toArray(new Double[loopsGains.size()]);
    }

    public Double[] getNonTouchingGains() {
        return nonTouchingLoopGains.toArray(new Double[nonTouchingLoopGains.size()]);
    }

}