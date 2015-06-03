import java.util.HashMap;

/**
 * Created by xavierwoo on 2015/6/1.
 */
public class Demo {
    static public void main(String argc[]){
        LPS solver = new LPS();

        solver.set_obj_type(1);

        HashMap<String, Double> c = new HashMap<>();
        c.put("x1", 1.0);
        c.put("x3", -3.0);
        c.put("x4", 3.0);
        solver.add_constraint(c, 6);

        c = new HashMap<>();
        c.put("x2", 1.0);
        c.put("x3", -8.0);
        c.put("x4", 4.0);
        solver.add_constraint(c, 4);

        solver.set_obj_coef("x3", -3);
        solver.set_obj_coef("x4", 1);
        solver.set_obj_constant(20);

        solver.solve();
        solver.print_solution();
    }
}
