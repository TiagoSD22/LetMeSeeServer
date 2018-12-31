package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

public class GifCreator {
	
	private final int LOOP_COUNT = 0;

	public byte[] createGif(ArrayList<BufferedImage> frames, ArrayList<Integer> delays,ByteArrayOutputStream baos) throws IOException {
		try {
			ImageOutputStream ios = ImageIO.createImageOutputStream(baos); 
			ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();	
			iw.setOutput(ios);
			iw.prepareWriteSequence(null);
			int index = 0;
			ImageWriteParam iwp = iw.getDefaultWriteParam();
			String frameDelay = "";
			for (BufferedImage frame : frames) {
				frameDelay = String.valueOf(delays.get(index) / 10L);
				IIOMetadata metadata = iw.getDefaultImageMetadata(new ImageTypeSpecifier(frame), iwp);
				configureMetaData(metadata, frameDelay, index++);
				iw.writeToSequence(new IIOImage(frame, null, metadata), null);
			}
			iw.endWriteSequence();
			ios.close();
			return baos.toByteArray();
		} finally {
			frames = null;
			baos = null;
		}
	}
	
	public void addFrame(ArrayList<BufferedImage> frames, BufferedImage image) {
		frames.add(image);	   
	}

	private void configureMetaData(IIOMetadata meta, String delayTime, int imageIndex) throws IIOInvalidTreeException {
		String metaFormat = meta.getNativeMetadataFormatName();
		Node root = meta.getAsTree(metaFormat);
		Node child = root.getFirstChild();
		while (child != null) {
			if ("GraphicControlExtension".equals(child.getNodeName())) {
				break;
			}
			child = child.getNextSibling();
		}
		IIOMetadataNode gce = (IIOMetadataNode) child;
		gce.setAttribute("userDelay", "FALSE");
		gce.setAttribute("delayTime", delayTime);
		gce.setAttribute("disposalMethod", "none");

		if (imageIndex == 0) {
			IIOMetadataNode aes = new IIOMetadataNode("ApplicationExtensions");
			IIOMetadataNode ae = new IIOMetadataNode("ApplicationExtension");
			ae.setAttribute("applicationID", "NETSCAPE");
	        ae.setAttribute("authenticationCode", "2.0");
			byte[] uo = new byte[] { 0x1, (byte) (LOOP_COUNT & 0xFF), (byte) ((LOOP_COUNT >> 8) & 0xFF) };
			ae.setUserObject(uo);
			aes.appendChild(ae);
			root.appendChild(aes);
		}
		meta.setFromTree(metaFormat, root);		
	}
}
