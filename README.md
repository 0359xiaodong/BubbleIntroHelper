BubbleIntroHelper
=================

BubbleIntroHelper provides an easy way how to add bubble guided tour for your android application users. Checkout Instago app for an instance.

Showing a bubble for your view is provided by using the following method:

	/**
	 * Assign popup window to the specific parent view. Once the window is shown it will not be shown in the future anymore. 
	 * To provide popup's showing again one must clear app's storage data.
	 * 
	 * 
	 * @param forVersionCode   App version code for which popup will be shown
	 * @param ID   An ID of a popup window
	 * @param parent   A parent view to which this popup window will belong
	 * @param content   Context
	 * @return
	 */
	public static synchronized HelpPopupWindow addHelpPopupWindow(final int forVersionCode, final int ID,
			final View parent, final String content)

![Alt text](https://drive.google.com/uc?export=download&id=0B4jPPQOZ8N-MWHlwRzdYV1llR3c)
