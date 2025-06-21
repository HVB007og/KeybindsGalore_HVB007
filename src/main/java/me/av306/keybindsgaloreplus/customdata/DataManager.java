package me.av306.keybindsgaloreplus.customdata;

import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.LOGGER;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Hashtable;

public class DataManager
{
    private final File dataFile;

    public final Hashtable<String, KeybindData> customData = new Hashtable<>();

    /**
     * True if the data file is present AND was read successfully
     */
    public boolean hasCustomData = true;

    public DataManager( Path dataFilePath, String dataFileName )
    {
        this.dataFile = dataFilePath.resolve( dataFileName ).toFile();

        if ( !this.dataFile.exists() )
        {
            this.hasCustomData = false;
            LOGGER.warn( "(KBG+ Custom Data Manager) No custom keybind data file found!" );
            return;
        }

        this.readDataFile();
    }

    public void readDataFile()
    {
        this.hasCustomData = true;
        try ( BufferedReader fileReader = new BufferedReader( new FileReader( this.dataFile ) ); )
        {
            String line;
            String currentKeybind = null;
            while ( true )
            {
                line = fileReader.readLine();

                if ( line == null ) break;
                // Skip blank lines
                if ( line.isBlank() ) continue;

                // Indented line -- is property
                // Python moment
                if ( !line.endsWith( ":" ) )
                {
                    String[] lines = line.trim().split( "=" );
                    try
                    {
                        lines[1] = lines[1].replaceAll( "\"", "" );

                        switch ( lines[0] )
                        {
                            case "display_name" -> this.customData.get( currentKeybind ).displayName = lines[1];
                            case "sector_color" -> this.customData.get( currentKeybind ).sectorColor = Integer.parseInt( lines[1].replace( "0x", "" ), 16 );
                            case "hide_category" -> this.customData.get( currentKeybind ).hideCategory = Boolean.parseBoolean( lines[1] );
                            default -> LOGGER.info( "(KBG+ Custom Data Manager) Unknown custom data field: {}", lines[0] );
                        }

                        //LOGGER.info( "(KBG+ Custom Data Manager) Set \"{}\" of keybind {} to \"{}\"", lines[0], currentKeybind, lines[1] );
                    }
                    catch ( ArrayIndexOutOfBoundsException oobe )
                    {
                        LOGGER.warn( "(KBG+ Custom Data Manager) Skipped invalid data line: {}", line );
                    }
                }
                else
                {
                    // Non-indented line -- is header
                    currentKeybind = line.replaceAll( "[\":]+", "" ).trim();
                    this.customData.put( currentKeybind, new KeybindData() );
                    LOGGER.info( "(KBG+ Custom Data Manager) Reading custom data for keybind: {}", currentKeybind );
                }
            }

            // Finished reading file
            //this.hasCustomData = true;
            LOGGER.info( "(KBG+ Custom Data Manager) Custom keybind data file read successfully!" );
        }
        catch ( IOException ioe )
        {
            // IOE -- usually file not found
            this.hasCustomData = false;

            LOGGER.warn( "(KBG+ Custom Data Manager) IOException while reading custom data: {}", ioe.getMessage() );
            //ioe.printStackTrace();
        }
        
        if ( Configurations.DEBUG )
        {
            // Dump data table
            LOGGER.info( "(KBG+ Custom Data Manager) Custom data present: {}", this.hasCustomData );

            this.customData.forEach( (keyId, data) ->
            {
                for ( Field f : data.getClass().getDeclaredFields() )
                {
                    try
                    {
                        //f.setAccessible( true );
                        LOGGER.info( "\t[{} -> {}]: {}", keyId, f.getName(), f.get( data ) );
                    }
                    catch ( IllegalAccessException e )
                    {
                        e.printStackTrace();
                    }
                }
            } );
        }
    }
}
