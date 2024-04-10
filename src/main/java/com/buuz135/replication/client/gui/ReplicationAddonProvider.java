package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.api.client.assets.types.IBackgroundAsset;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class ReplicationAddonProvider implements IAssetProvider {

    public static ResourceLocation DEFAULT_LOCATION = new ResourceLocation(Replication.MOD_ID, "textures/gui/background.png");
    public static ReplicationAddonProvider INSTANCE = new ReplicationAddonProvider();

    private final IAsset SLOT = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(1, 185, 18, 18);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset ENERGY_BAR = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(177, 94, 18, 56);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset ENERGY_FILL = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 97, 12, 50);
        }

        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final ITankAsset TANK_NORMAL = new ITankAsset() {
        public int getFluidRenderPadding(Direction facing) {
            return 3;
        }

        public Rectangle getArea() {
            return new Rectangle(177, 1, 18, 56);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final ITankAsset TANK_SMALL = new ITankAsset() {
        public int getFluidRenderPadding(Direction facing) {
            return 3;
        }

        public Rectangle getArea() {
            return new Rectangle(235, 1, 18, 19);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final Point HOTBAR_POS = new Point(8, 160);
    private final Point INV_POS = new Point(8, 102);
    private final IBackgroundAsset BACKGROUND = new IBackgroundAsset() {
        public Point getInventoryPosition() {
            return ReplicationAddonProvider.this.INV_POS;
        }

        public Point getHotbarPosition() {
            return ReplicationAddonProvider.this.HOTBAR_POS;
        }

        public Rectangle getArea() {
            return new Rectangle(0, 0, 176, 184);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_BACKGROUND = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(229, 1, 5, 50);
        }

        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_FILL = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(223, 1, 5, 50);
        }

        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_BORDER = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(211, 1, 11, 56);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_DISABLED = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 1, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_ENABLED = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 16, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_PULL = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 31, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_PUSH = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 46, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_MANAGER = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(1, 231, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_ARROW_HORIZONTAL = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(177, 77, 22, 16);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(177, 61, 22, 15);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset AUGMENT_BACKGROUND = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(212, 61, 30, 84);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_ARROW_UP = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(177, 151, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_ARROW_RIGHT = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(192, 151, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_ARROW_DOWN = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(207, 151, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_ARROW_LEFT = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(222, 151, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset ITEM_BACKGROUND = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(177, 166, 18, 18);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset TEXT_FIELD_ACTIVE = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(31, 240, 110, 16);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset TEXT_FIELD_INACTIVE = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(142, 240, 110, 16);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_REDSTONE_IGNORED = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(196, 166, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_REDSTONE_NO_REDSTONE = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(226, 166, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_REDSTONE_REDSTONE = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(211, 166, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_REDSTONE_ONCE = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(241, 166, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_ARROW_DOWN = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(221, 211, 15, 23);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_BACKGROUND_ARROW_DOWN = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(221, 185, 15, 23);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset HUE_PICKER = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(235, 21, 9, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset SHADER_PICKER = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(245, 21, 9, 9);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_LOCKED = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(241, 196, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };
    private final IAsset BUTTON_UNLOCKED = new IAsset() {
        public Rectangle getArea() {
            return new Rectangle(241, 181, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return DEFAULT_LOCATION;
        }
    };

    public ReplicationAddonProvider() {
    }

    @Nullable
    public <T extends IAsset> T getAsset(IAssetType<T> assetType) {
        if (assetType == AssetTypes.BACKGROUND) {
            return assetType.castOrDefault(this.BACKGROUND);
        } else if (assetType == AssetTypes.ENERGY_BACKGROUND) {
            return assetType.castOrDefault(this.ENERGY_BAR);
        } else if (assetType == AssetTypes.ENERGY_BAR) {
            return assetType.castOrDefault(this.ENERGY_FILL);
        } else if (assetType == AssetTypes.PROGRESS_BAR_BACKGROUND_VERTICAL) {
            return assetType.castOrDefault(this.PROGRESS_BAR_BACKGROUND);
        } else if (assetType == AssetTypes.PROGRESS_BAR_VERTICAL) {
            return assetType.castOrDefault(this.PROGRESS_BAR_FILL);
        } else if (assetType == AssetTypes.SLOT) {
            return assetType.castOrDefault(this.SLOT);
        } else if (assetType == AssetTypes.TANK_NORMAL) {
            return assetType.castOrDefault(this.TANK_NORMAL);
        } else if (assetType == AssetTypes.TANK_SMALL) {
            return assetType.castOrDefault(this.TANK_SMALL);
        } else if (assetType == AssetTypes.PROGRESS_BAR_BORDER_VERTICAL) {
            return assetType.castOrDefault(this.PROGRESS_BAR_BORDER);
        } else if (assetType == AssetTypes.BUTTON_SIDENESS_DISABLED) {
            return assetType.castOrDefault(this.BUTTON_SIDENESS_DISABLED);
        } else if (assetType == AssetTypes.BUTTON_SIDENESS_ENABLED) {
            return assetType.castOrDefault(this.BUTTON_SIDENESS_ENABLED);
        } else if (assetType == AssetTypes.BUTTON_SIDENESS_PULL) {
            return assetType.castOrDefault(this.BUTTON_SIDENESS_PULL);
        } else if (assetType == AssetTypes.BUTTON_SIDENESS_PUSH) {
            return assetType.castOrDefault(this.BUTTON_SIDENESS_PUSH);
        } else if (assetType == AssetTypes.BUTTON_SIDENESS_MANAGER) {
            return assetType.castOrDefault(this.BUTTON_SIDENESS_MANAGER);
        } else if (assetType == AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL) {
            return assetType.castOrDefault(this.PROGRESS_BAR_BACKGROUND_ARROW_HORIZONTAL);
        } else if (assetType == AssetTypes.PROGRESS_BAR_ARROW_HORIZONTAL) {
            return assetType.castOrDefault(this.PROGRESS_BAR_ARROW_HORIZONTAL);
        } else if (assetType == AssetTypes.AUGMENT_BACKGROUND) {
            return assetType.castOrDefault(this.AUGMENT_BACKGROUND);
        } else if (assetType == AssetTypes.BUTTON_ARROW_LEFT) {
            return assetType.castOrDefault(this.BUTTON_ARROW_LEFT);
        } else if (assetType == AssetTypes.BUTTON_ARROW_RIGHT) {
            return assetType.castOrDefault(this.BUTTON_ARROW_RIGHT);
        } else if (assetType == AssetTypes.BUTTON_ARROW_UP) {
            return assetType.castOrDefault(this.BUTTON_ARROW_UP);
        } else if (assetType == AssetTypes.BUTTON_ARROW_DOWN) {
            return assetType.castOrDefault(this.BUTTON_ARROW_DOWN);
        } else if (assetType == AssetTypes.ITEM_BACKGROUND) {
            return assetType.castOrDefault(this.ITEM_BACKGROUND);
        } else if (assetType == AssetTypes.TEXT_FIELD_ACTIVE) {
            return assetType.castOrDefault(this.TEXT_FIELD_ACTIVE);
        } else if (assetType == AssetTypes.TEXT_FIELD_INACTIVE) {
            return assetType.castOrDefault(this.TEXT_FIELD_INACTIVE);
        } else if (assetType == AssetTypes.BUTTON_REDSTONE_IGNORED) {
            return assetType.castOrDefault(this.BUTTON_REDSTONE_IGNORED);
        } else if (assetType == AssetTypes.BUTTON_REDSTONE_NO_REDSTONE) {
            return assetType.castOrDefault(this.BUTTON_REDSTONE_NO_REDSTONE);
        } else if (assetType == AssetTypes.BUTTON_REDSTONE_REDSTONE) {
            return assetType.castOrDefault(this.BUTTON_REDSTONE_REDSTONE);
        } else if (assetType == AssetTypes.BUTTON_REDSTONE_ONCE) {
            return assetType.castOrDefault(this.BUTTON_REDSTONE_ONCE);
        } else if (assetType == AssetTypes.PROGRESS_BAR_ARROW_DOWN) {
            return assetType.castOrDefault(this.PROGRESS_BAR_ARROW_DOWN);
        } else if (assetType == AssetTypes.PROGRESS_BAR_BACKGROUND_ARROW_DOWN) {
            return assetType.castOrDefault(this.PROGRESS_BAR_BACKGROUND_ARROW_DOWN);
        } else if (assetType == AssetTypes.HUE_PICKER) {
            return assetType.castOrDefault(this.HUE_PICKER);
        } else if (assetType == AssetTypes.SHADE_PICKER) {
            return assetType.castOrDefault(this.SHADER_PICKER);
        } else if (assetType == AssetTypes.BUTTON_LOCKED) {
            return assetType.castOrDefault(this.BUTTON_LOCKED);
        } else {
            return assetType == AssetTypes.BUTTON_UNLOCKED ? assetType.castOrDefault(this.BUTTON_UNLOCKED) : null;
        }
    }
}

