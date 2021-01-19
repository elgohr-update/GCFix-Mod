package com.pam.harvestcraft.gui;

import com.pam.harvestcraft.HarvestCraft;
import com.pam.harvestcraft.tileentities.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.pam.harvestcraft.proxy.PacketHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraft.client.gui.GuiTextField;

public class CustomGuiMarket extends GuiContainer {
	private static final ResourceLocation gui = new ResourceLocation("harvestcraft:textures/gui/market.png");

	private int itemNum;

	private final TileEntityMarket tileEntityMarket;

	public CustomGuiMarket(InventoryPlayer inventoryplayer, TileEntityMarket tileEntityMarket) {
		super(new ContainerMarket(inventoryplayer, tileEntityMarket));
		this.tileEntityMarket = tileEntityMarket;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(false);

		buttonList.clear();

		final int posX = width / 2 - 48;
		final int posY = height / 2 - 48;

		final GuiButton left = new GuiButton(0, posX, posY - 21, 15, 20, "<");
		final GuiButton right = new GuiButton(1, posX + 16, posY - 21, 15, 20, ">");
		final GuiButton button_buy = new GuiButton(2, posX, posY + 1, 55, 20, "Buy");
		final GuiButton button_search = new GuiButton(3, posX + 16, posY - 5, 30, 20, "Search");
		
		final GuiTextField text_search = new GuiTextField(4, posX - 16, posY - 5, 30, 20);

		buttonList.add(left);
		buttonList.add(right);
		buttonList.add(button_buy);
		buttonList.add(button_search);

		this.itemNum = tileEntityMarket.getBrowsingInfo();
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(!guibutton.enabled) {
			return;
		}
		if(guibutton.id == 0) {
			itemNum--;
			if(itemNum < 0) {
				itemNum = MarketItems.getSize() - 1;
			}
			this.tileEntityMarket.setBrowsingInfo(itemNum);
			PacketHandler.network.sendToServer(new MessageMarketBrowse(itemNum));
		}
		if(guibutton.id == 1) {
			itemNum++;
			if(itemNum > MarketItems.getSize() - 1) {
				itemNum = 0;
			}
			this.tileEntityMarket.setBrowsingInfo(itemNum);
			PacketHandler.network.sendToServer(new MessageMarketBrowse(itemNum));
		}
		if(guibutton.id == 2) {
			PacketHandler.network.sendToServer(new MessageMarketBuy(this.itemNum));
		}
		if(guibutton.id == 3) {
			System.out.println("Hello, Minecraft!");
		}
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString("Inventory", 8, (ySize - 96) + 13, 4210752);

		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		itemRender.zLevel = 100.0F;

		MarketData data = MarketItems.getData(itemNum);

		ItemStack item = data.getItem();
		itemRender.renderItemAndEffectIntoGUI(item, 73, 16);
		itemRender.renderItemOverlayIntoGUI(fontRenderer, item, 73, 16, "");

		ItemStack currency = data.getCurrency();
		itemRender.renderItemAndEffectIntoGUI(currency, 100, 16);
		itemRender.renderItemOverlayIntoGUI(fontRenderer, currency, 100, 16, "");
		itemRender.zLevel = 0.0F;
		GL11.glDisable(GL11.GL_LIGHTING);

		int price = data.getPrice();
		this.fontRenderer.drawString("x" + Integer.toString(price), 116, 20, 0);

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		super.drawScreen(par1, par2, par3);
		ItemStack item = MarketItems.getData(itemNum).getItem();

		if(this.isPointInRegion(73, 16, 16, 16, par1, par2)) {
			this.renderToolTip(item, par1, par2);
		}
	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
		int l = (width - xSize) / 2;
		int i1 = (height - ySize) / 2;
		this.drawTexturedModalRect(l, i1 - 10, 0, 0, xSize, ySize + 21);
	}
}