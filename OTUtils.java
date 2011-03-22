/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

/**
 * User: az
 * Date: 03.03.11
 * Time: 10:32
 */
public class OTUtils {

	static final Utils.Function<OTEntity,OperatorTree> entityToTreeFunc = new Utils.Function<OTEntity,OperatorTree>() {
		public OperatorTree eval(OTEntity obj) {
			return obj.asTree();
		}
	};

}
