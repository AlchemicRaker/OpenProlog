package com.syntheticentropy.ocpro;

import li.cil.oc.api.Machine;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = OpenProlog.MODID, name = OpenProlog.NAME,
        version = OpenProlog.VERSION, dependencies = "required-after:opencomputers@[1.7.4,)")
public class OpenProlog
{
    @SuppressWarnings("WeakerAccess")
    public static final String MODID = "ocpro";
    @SuppressWarnings("WeakerAccess")
    public static final String NAME = "OpenProlog";
    @SuppressWarnings("WeakerAccess")
    public static final String VERSION = "1.0";

    // resource path
    static final String RESOURCE_PATH = "/com/syntheticentropy/ocpro/";

    protected static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        Machine.add(PrologArchitecture.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
//        // some example code
//        Machine.architectures().forEach(arch -> {
//            logger.info("Architecture FOUND: {}", arch.getName());
//        });
//        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
