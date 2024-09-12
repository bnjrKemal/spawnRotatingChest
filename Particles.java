package com.bnjrKemal.advantureChest;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static com.bnjrKemal.advantureChest.AdvantureChest.getInstance;

public class Particles {

    /**
     Creates a chest drop animation from 50 blocks above this location.
     */
    public static void spawnRotatingChest(Location location) {
        // ArmorStand oluştur ve ayarlarını yap
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 50, 0), EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setGravity(false); // Yerçekimi kapalı (manüel süzülme)
        armorStand.getEquipment().setHelmet(new ItemStack(Material.CHEST)); // Başına sandık koy

        double targetY = location.getY(); // Hedef yüksekliği al

        // Yeni bir BukkitRunnable ile döndürme, particle efekti ve süzülme kontrolü
        new BukkitRunnable() {
            float yaw = 0; // ArmorStand'in rotasyonu
            final double fallSpeed = 0.2; // ArmorStand'in yavaş düşme hızı

            @Override
            public void run() {
                // 1. Zırh Standının döndürülmesi
                yaw = (yaw + 5) % 360; // Yaw değerini 360 ile sınırla
                armorStand.setRotation(yaw, 0);

                // 2. Particle Efekti Ekleme
                Particle.DustTransition dustTransition = new Particle.DustTransition(Color.GREEN, Color.GRAY, 1);
                armorStand.getWorld().spawnParticle(Particle.DUST, armorStand.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.05, dustTransition);

                // 3. Süzülme işlemi (yavaş yavaş düşmesini sağla)
                Location currentLocation = armorStand.getLocation().clone();
                currentLocation.add(0, -fallSpeed, 0); // Y ekseni boyunca yavaşça düşme
                armorStand.teleport(currentLocation); // Yeni konuma teleport et

                // 4. Zemin Kontrolü (hedef yüksekliğe ulaşıldığında)
                if (currentLocation.getY() <= targetY) {
                    // Eğer hedef yüksekliğe ulaşıldıysa:

                    // ArmorStand'i yok et
                    armorStand.remove();

                    // Yere sandık koy
                    currentLocation.getBlock().setType(Material.CHEST);

                    // Bu görevi iptal et
                    cancel();
                }
            }
        }.runTaskTimer(getInstance(), 0L, 1L); // 0L başlangıç, 1L her tick (20 tick = 1 saniye)
    }

}
