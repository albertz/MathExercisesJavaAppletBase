package applets.Termumformungen$in$der$Technik_05_Fadenstrahlrohr;

/**
 * User: az
 * Date: 18.03.11
 * Time: 12:36
 */
public class VTHintContainer extends VTContainer {

	VisualThing[] hintContent;

	public VTHintContainer(int stepX, int stepY, VisualThing[] things) {
		super(stepX, stepY, null);
		hintContent = things;
		initHintInitial();
	}

	void initHintInitial() {
		things = new VisualThing[] {
			new VTButton("Hinweis zeigen", 0, 0, new Runnable() {
				public void run() {
					things = hintContent;
					panel.removeAll();
					panel.revalidate();
				}
			})
		};
	}

}
