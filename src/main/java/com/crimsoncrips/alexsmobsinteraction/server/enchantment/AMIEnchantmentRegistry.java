package com.crimsoncrips.alexsmobsinteraction.server.enchantment;


import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemShieldOfTheDeep;
import com.github.alexthe666.alexsmobs.item.ItemTendonWhip;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMIEnchantmentRegistry {


    public static final DeferredRegister<Enchantment> DEF_REG;

    public static final EnchantmentCategory TENDON_WHIP;

    public static final EnchantmentCategory ROLLER;


    public static final RegistryObject<Enchantment> STABILIZER;

    public static final RegistryObject<Enchantment> LIGHTWEIGHT;

    public static final RegistryObject<Enchantment> ROLLING_THUNDER;

    public static final RegistryObject<Enchantment> STRETCHY_ACCUMULATION;

    public AMIEnchantmentRegistry() {
    }


    static {
        DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "alexsmobsinteraction");
        TENDON_WHIP = EnchantmentCategory.create("tendon_whip", (item) -> {
            return item instanceof ItemTendonWhip;
        });

        ROLLER  = EnchantmentCategory.create("rocky_chestplate", item -> item == AMItemRegistry.ROCKY_CHESTPLATE.get());

        LIGHTWEIGHT = DEF_REG.register("lightweight", () -> new AMIBasicEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST,EquipmentSlot.CHEST));
        ROLLING_THUNDER = DEF_REG.register("rolling_thunder", () -> new AMIBasicEnchantment(Enchantment.Rarity.VERY_RARE, ROLLER,EquipmentSlot.CHEST));

        STABILIZER = DEF_REG.register("stabilizer", () -> new AMIStabilizerEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));
        STRETCHY_ACCUMULATION = DEF_REG.register("stretchy_accumulation", () -> {
            return new AMIBasicEnchantment(Enchantment.Rarity.VERY_RARE, TENDON_WHIP, EquipmentSlot.MAINHAND);
        });
    }
}
