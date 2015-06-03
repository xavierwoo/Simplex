import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simplex algorithm
 * Created by xavierwoo on 2015/6/1.
 */
public class LPS {

    private double Z[];
    private double A[][];
    private double V[];

    private HashMap<Integer, Integer> var_constraint_map = new HashMap<>();
    private HashMap<Integer, Integer> constraint_var_map = new HashMap<>();
    //private double bound[];

    private int obj_type;
    private HashMap<String, Double> obj_var_coef = new HashMap<>();
    private double obj_constant = 0;

    private ArrayList<HashMap<String, Double>> constraint_var_coef = new ArrayList<>();
    private ArrayList<Double> constraint_constant = new ArrayList<>();

    //HashMap<String, Integer> var_bound = new HashMap<>();

    HashMap<String, Integer> var_map = new HashMap<>();

    int number_of_var = 0;

    public void set_obj_type(int type){
        if(type != -1 && type != 1){
            throw new UnsupportedOperationException("type must equals 1 or -1!");
        }
        obj_type = type;
    }

    public void set_obj_coef(String var_name, double coef){
        if(var_map.get(var_name) == null){
            var_map.put(var_name, number_of_var);
            ++number_of_var;
        }
        obj_var_coef.put(var_name, coef);
    }

    public void set_obj_constant(double c){
        obj_constant = c;
    }

    public void add_constraint(HashMap<String, Double> var_coef, double c){
        for(Map.Entry<String, Double> entry : var_coef.entrySet()){
            String var_name = entry.getKey();
            if(var_map.get(var_name) == null){
                var_map.put(var_name, number_of_var);
                ++number_of_var;
            }
        }
        constraint_var_coef.add(var_coef);
        constraint_constant.add(c);
    }

    private void set_var_constrait_map(){
        constraint_var_map.clear();
        var_constraint_map.clear();
        for(int i=0; i<A.length; i++){
            for(int j=0; j<A[i].length; j++){
                if(A[i][j]==1){
                    constraint_var_map.put(i,j);
                    var_constraint_map.put(j,i);
                }
            }
        }
    }

    private void trans_to_array_format(){
        Z = new double[var_map.size()+1];
        A = new double[constraint_var_coef.size()][var_map.size()+1];

        for(Map.Entry<String, Double> entry : obj_var_coef.entrySet()){
            int var_index = var_map.get(entry.getKey());
            double coef = entry.getValue();
            Z[var_index] = coef;
        }
        Z[Z.length-1] = obj_constant;

        for(int i=0; i<constraint_var_coef.size(); i++){
            HashMap<String, Double> var_coef = constraint_var_coef.get(i);
            for(Map.Entry<String, Double> entry : var_coef.entrySet()){
                int var_index = var_map.get(entry.getKey());
                double coef = entry.getValue();
                A[i][var_index] = coef;
            }
            A[i][A[i].length - 1] = constraint_constant.get(i);
        }
        obj_var_coef = null;
        constraint_var_coef = null;
        constraint_constant = null;
    }

    private void initialization(){
        trans_to_array_format();
        set_var_constrait_map();
    }

    private boolean unboundedness_criterion(){
        if(obj_type==1){
            each_var:
            for(int i=0; i<Z.length-1; i++){
                if(Z[i] > 0){
                    for (double[] aA : A) {
                        if (aA[i] > 0) {
                            break each_var;
                        }
                    }
                    return true;
                }
            }
        }else if(obj_type==-1){
            throw new UnsupportedOperationException("Do not write yet!");
        }else{
            throw new UnsupportedOperationException("No such obj type!");
        }
        return false;
    }
    private boolean optimality_criterion(){
        if(obj_type==1){
            for(int i=0; i<Z.length-1; i++){
                if(Z[i] > 0){
                    return false;
                }
            }
        }else if(obj_type == -1){
            throw new UnsupportedOperationException("Do not write yet!");
        }else{
            throw new UnsupportedOperationException("No such obj type!");
        }
        return true;
    }

    private void get_all_var_value(){
        V = new double[Z.length-1];
        for(int i=0; i<Z.length-1; i++){
            if(Z[i] != 0){
                V[i] = 0;
            }else{
                int c_index = var_constraint_map.get(i);
                V[i] = A[c_index][A[c_index].length-1];
            }
        }
    }

    private double get_bound(int c_index, int p_var_index){
        if(Z[p_var_index] > 0){
            return A[c_index][A[c_index].length-1] / A[c_index][p_var_index];
        }else{
            throw new UnsupportedOperationException("Do not write yet!");
        }
    }

    private void pivot(int var_index){

        //find the best bound
        int best_c_index = -1;
        double best_bound = 0;
        for(int i=0; i<A.length; i++){
            double bound = get_bound(i, var_index);
            if(best_c_index == -1 || (bound > 0 && bound < best_bound)){
                best_c_index = i;
                best_bound = bound;
            }
        }

        //pivot
        int pre_nbv_index = constraint_var_map.get(best_c_index);
        constraint_var_map.remove(best_c_index);
        var_constraint_map.remove(pre_nbv_index);
        constraint_var_map.put(best_c_index, var_index);
        var_constraint_map.put(var_index, best_c_index);

        //var_index will become basic variable
        double var_coef = A[best_c_index][var_index];
        for(int i=0; i<A[best_c_index].length; i++){
            A[best_c_index][i] /= var_coef;
        }
        for(int i=0; i<A.length; i++){
            if(i==best_c_index){
                continue;
            }
            double var_coef_i = A[i][var_index];
            for(int j=0; j<A[i].length; j++){
                A[i][j] -= A[best_c_index][j] * var_coef_i;
            }
        }
        double var_coef_obj = Z[var_index];
        for(int i=0; i<Z.length-1; i++){
            Z[i] -= A[best_c_index][i] * var_coef_obj;
        }
        Z[Z.length-1] += A[best_c_index][A[best_c_index].length-1] * var_coef_obj;
    }

    private int find_pivot_var(){
        if(obj_type == 1){
            for(int i=0; i<Z.length-1; i++){
                if(Z[i] > 0){
                    for(double aA[] : A){
                        if(aA[i] > 0){
                            return i;
                        }
                    }
                }
            }
        }else{
            throw new UnsupportedOperationException("Not write yet!");
        }
        return -1;
    }

    public void print_solution(){
        System.out.println("Objective: " + Z[Z.length-1]);
        get_all_var_value();
        for(Map.Entry<String, Integer> entry : var_map.entrySet()){
            String var_name = entry.getKey();
            int var_index = entry.getValue();
            System.out.println(var_name + "\t" + V[var_index]);
        }
    }

    public boolean solve(){
        initialization();

        if(unboundedness_criterion()){
            System.out.println("The problem is unbounded!");
            return false;
        }

        while(!optimality_criterion()){
            int var_index = find_pivot_var();
            pivot(var_index);
        }
        return true;
    }
}
