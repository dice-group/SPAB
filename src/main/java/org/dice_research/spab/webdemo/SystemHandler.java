package org.dice_research.spab.webdemo;

public class SystemHandler extends AbstractHandler {

	@Override
	public void handle() throws WebserverIoException {

		Runtime runtime = Runtime.getRuntime();

		StringBuilder htmlBuilder = new StringBuilder("<h2>System</h2>");
		htmlBuilder.append("<ul>");

		htmlBuilder.append("<li>");
		addMiB(htmlBuilder, runtime.totalMemory());
		htmlBuilder.append(" total memory");
		htmlBuilder.append("</li>");

		htmlBuilder.append("<li>");
		addMiB(htmlBuilder, runtime.freeMemory());
		htmlBuilder.append(" free memory");
		htmlBuilder.append("</li>");

		htmlBuilder.append("<li>");
		addMiB(htmlBuilder, runtime.totalMemory() - runtime.freeMemory());
		htmlBuilder.append(" used memory");
		htmlBuilder.append("</li>");

		htmlBuilder.append("<li>");
		addMiB(htmlBuilder, runtime.maxMemory());
		htmlBuilder.append(" max memory");
		htmlBuilder.append("</li>");

		htmlBuilder.append("<li>" + runtime.availableProcessors() + " available processors" + "</li>");
		htmlBuilder.append("</ul>");

		setOkWithBody(htmlBuilder.toString());
	}

	protected void addMiB(StringBuilder htmlBuilder, long bytes) {
		htmlBuilder.append(bytes / 1024 / 1024);
		htmlBuilder.append(" MiB");
	}
}