import java.util.*;

public final class CalcMath {

    private static double[][] multScalar(double[][] matrix, double scale){
        double[][] newMatrix = new double[matrix.length][matrix[0].length];
        for(int row=0 ; row< matrix.length ; row++){
            for (int column=0 ; column<matrix[row].length ; column++){
                newMatrix[row][column]=matrix[row][column]*scale;
            }
        }
        return newMatrix;
    }

    private static double[][] addScalar(double[][] matrix, double add){
        double[][] newMatrix = new double[matrix.length][matrix[0].length];
        for(int row=0 ; row< matrix.length ; row++){
            for (int column=0 ; column<matrix[row].length ; column++){
                newMatrix[row][column]=matrix[row][column]+add;
            }
        }
        return newMatrix;
    }

    private static double[][] multMatrix(double[][] a, double[][] b){
        if(a[0].length!=b.length){ //check for compatibility
            return null;
        }
        double numB=0;
        double numA=0;
        double multNum=0;
        double[][] matrix = new double[a.length][b[0].length];
        for (int row=0; row<a.length ; row++){
            for(int colB=0 ; colB<b[0].length ; colB++){
                for(int colA=0 ; colA<a[row].length ; colA++){
                    numB=b[colA][colB];
                    numA=a[row][colA];
                    multNum+=(numA*numB);
                }
                matrix[row][colB]=multNum;
                multNum=0;
            }
        }
        return matrix;
    }

    private static double euclideanDist(double[][] a, double[][] b){
        double dist=0;
        double euclDist=0;
        if(a.length!=b.length){ //check for compatibility
            return -1;
        }
        for(int point=0 ; point<a.length ; point++){
            for(int num=0 ; num<a[point].length ; num++){
                dist+=Math.pow((a[point][num]-b[point][num]),2);
            }
        }
        euclDist=Math.sqrt(dist);
        return euclDist;
    }

    public static double[][] pageRankVector(ArrayList<Page> pages, int N){
        double[][] adjMatrix = new double[N][N];
        double alpha = 0.1;
        HashSet<Page> outgoing;
        int numOutgoing=0;
        int num = -1;
        double threshold=1;
        double[][] tnew = new double[1][N];

        for(Page page : pages){
            outgoing = page.getOutgoingPages();
            numOutgoing = outgoing.size();
            num = page.getFileNum();
            if(numOutgoing==0){ //Check if the page has no outgoing links
                Arrays.fill(adjMatrix[num], (double)1/N);
                continue;
            }
            for(Page otherPage : pages){
                if(outgoing.contains(otherPage)){
                    adjMatrix[num][otherPage.getFileNum()] = (double)1/numOutgoing; //set the outgoing page value to 1/numOutgoing
                }
            }
        }

        adjMatrix = multScalar(adjMatrix, 1-alpha); //multiply everything by 1-alpha
        adjMatrix = addScalar(adjMatrix, alpha/N); //add alpha/N to everything

        //initialize the first vector
        Arrays.fill(tnew[0], (double) 1 / N);

        while (threshold > 0.0001) {
            double[][] tprev = tnew;
            tnew = multMatrix(tprev, adjMatrix);
            threshold = euclideanDist(tprev,tnew);
        }

        return tnew;
    }

    private static double calcEuclideanNorm(double[] vector, int index){
        if(index== vector.length){
            return 0;
        }
        else if(index==0){
            return Math.sqrt(vector[index]*vector[index] + calcEuclideanNorm(vector, index+1));
        }
        return vector[index]*vector[index] + calcEuclideanNorm(vector, index+1);
    }

    private static double calcNumerator(double[] vectorA, double[] vectorB, int index){
        if(index==vectorA.length){
            return 0;
        }
        return vectorA[index]*vectorB[index] + calcNumerator(vectorA, vectorB, index+1);
    }

    public static double calcCosineSimilarity(double[] vectorA, double[] vectorB){
        double numerator = calcNumerator(vectorA, vectorB, 0);
        double leftDenom = calcEuclideanNorm(vectorA, 0);
        double rightDenom = calcEuclideanNorm(vectorB, 0);
        if (leftDenom==0 || rightDenom==0){
            return 0.0;
        }
        return numerator/(leftDenom*rightDenom);
    }

}
