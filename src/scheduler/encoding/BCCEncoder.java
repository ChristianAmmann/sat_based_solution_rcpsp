package scheduler.encoding;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

public class BCCEncoder {

	private static BCCEncoder encoder;
	private VariableFactory variableFactory;

	private BCCEncoder() {
		variableFactory = VariableFactory.getVariableFactory();
	}

	public static BCCEncoder getBCCEncoder() {
		if (encoder != null) {
			return encoder;
		}
		return new BCCEncoder();
	}

	private void genHalfAdder(ISolver solver, int a, int b, int sum, int carry)
			throws ContradictionException {
		solver.addClause(new VecInt(new int[] { a, neg(b), sum }));
		solver.addClause(new VecInt(new int[] { a, neg(b), sum }));
		solver.addClause(new VecInt(new int[] { neg(a), b, sum }));
		solver.addClause(new VecInt(new int[] { neg(a), neg(b), carry }));

	}

	private void genFullAdder(ISolver solver, int a, int b, int c, int sum,
			int carry) throws ContradictionException {
		solver.addClause(new VecInt(new int[] { a, b, neg(c), sum }));
		solver.addClause(new VecInt(new int[] { a, neg(b), c, sum }));
		solver.addClause(new VecInt(new int[] { neg(a), b, c, sum }));
		solver.addClause(new VecInt(new int[] { neg(a), neg(b), neg(c), sum }));
		solver.addClause(new VecInt(new int[] { neg(a), neg(b), carry }));
		solver.addClause(new VecInt(new int[] { neg(a), neg(c), carry }));
	}

	private List<Integer> genParCounter(ISolver solver, List<Integer> input,
			List<Integer> output, int resourceId, int time)
			throws ContradictionException {
		int m = ilog2(input.size());
		if (input.size() == 1) {
			output.add(input.get(0));
			return output;
		}

		int p_end = (int) Math.pow(2.0, (double) m) - 1;

		List<Integer> a_inputs = new ArrayList<Integer>();
		List<Integer> b_inputs = new ArrayList<Integer>();
		List<Integer> a_outputs = new ArrayList<Integer>();
		List<Integer> b_outputs = new ArrayList<Integer>();

		for (int i = 0; i < p_end; i++) {
			a_inputs.add(input.get(i));
		}
		for (int i = p_end; i < input.size() - 1; i++) {
			b_inputs.add(input.get(i));
		}
		a_outputs = genParCounter(solver, a_inputs, a_outputs, resourceId, time);

		if (b_inputs.size() > 0) {
			b_outputs = genParCounter(solver, b_inputs, b_outputs, resourceId,
					time);
		}

		int m_min = Math.min(a_outputs.size(), b_outputs.size());
		int carry = input.get(input.size() - 1);

		for (int i = 0; i < m_min; i++) {
			Integer sum = variableFactory.sum(resourceId, time, i);
			Integer nextCarry = variableFactory.carry(resourceId, time, i);
			genFullAdder(solver, a_outputs.get(i), b_outputs.get(i), carry,
					sum, nextCarry);
			output.add(sum);
			carry = nextCarry;
		}

		for (int i = m_min; i < a_outputs.size(); i++) {
			Integer sum = variableFactory.sum(resourceId, time, i);
			Integer nextCarry = variableFactory.carry(resourceId, time, i);
			genHalfAdder(solver, a_outputs.get(i), carry, sum, nextCarry);
			output.add(sum);
			carry = nextCarry;
		}
		output.add(carry);
		return output;
	}

	public void genLessThenConstrait(ISolver solver, int bound,
			List<Integer> inputs, int resourceId, int time)
			throws ContradictionException {
		List<List<Integer>> clause = new ArrayList<List<Integer>>();
		List<Integer> outputs = genParCounter(solver, inputs,
				new ArrayList<Integer>(), resourceId, time);
		int b = bound & 1;
		if (b != 1) {
			List<Integer> c = new ArrayList<Integer>();
			c.add(neg(outputs.get(0)));
			clause.add(c);
		}
		bound >>= 1; // delete bit 0

		for (int i = 1; i < outputs.size(); i++) { // further bits
			int bit_i = bound & 1; // get LSB

			if (bit_i == 1) {
				for (int k = 0; k < clause.size(); k++) {
					clause.get(k).add(neg(outputs.get(i)));
				}
			} else {
				List<Integer> cc = new ArrayList<Integer>();
				cc.add(neg(outputs.get(i)));
				clause.add(cc);
			}
			bound >>= 1;
		}

		for (int i = 0; i < clause.size(); i++) {
			int[] newClause = new int[clause.get(i).size()];
			int m = 0;
			for (Integer k : clause.get(i)) {
				newClause[m] = k;
				m++;
			}
			solver.addClause(new VecInt(newClause));
		}
		;
	}

	private int ilog2(int number) {
		int log = -1;
		while (number > 0) {
			number >>= 1;
			log++;
		}
		return log;
	}

	private Integer neg(Integer var) {
		return -1 * var;
	}
}
