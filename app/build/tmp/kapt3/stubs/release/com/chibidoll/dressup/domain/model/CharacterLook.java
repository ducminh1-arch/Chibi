package com.chibidoll.dressup.domain.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 72\u00020\u0001:\u000267BY\b\u0011\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0014\u0010\u0004\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\u0014\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0005\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0002\u0010\u000eBI\u0012\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n0\u0005\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u000fJ\u0015\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0015\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003JM\u0010\u001b\u001a\u00020\u00002\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00072\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n0\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\u0015\u0010\u001f\u001a\u0004\u0018\u00010\n2\u0006\u0010 \u001a\u00020!\u00a2\u0006\u0002\u0010\"J\f\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$J\u0018\u0010&\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\u00060\'0$J\t\u0010(\u001a\u00020\u0003H\u00d6\u0001J\u000e\u0010)\u001a\u00020\u001d2\u0006\u0010*\u001a\u00020\u0007J\t\u0010+\u001a\u00020\u0006H\u00d6\u0001J\u0016\u0010,\u001a\u00020\u00002\u0006\u0010 \u001a\u00020!2\u0006\u0010-\u001a\u00020\nJ&\u0010.\u001a\u00020/2\u0006\u00100\u001a\u00020\u00002\u0006\u00101\u001a\u0002022\u0006\u00103\u001a\u000204H\u00c1\u0001\u00a2\u0006\u0002\b5R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001d\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u00068"}, d2 = {"Lcom/chibidoll/dressup/domain/model/CharacterLook;", "", "seen1", "", "equippedItems", "", "", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "backgroundItem", "layerColors", "", "presetId", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(ILjava/util/Map;Lcom/chibidoll/dressup/domain/model/CatalogItem;Ljava/util/Map;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "(Ljava/util/Map;Lcom/chibidoll/dressup/domain/model/CatalogItem;Ljava/util/Map;Ljava/lang/String;)V", "getBackgroundItem", "()Lcom/chibidoll/dressup/domain/model/CatalogItem;", "getEquippedItems", "()Ljava/util/Map;", "getLayerColors", "getPresetId", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "getColorForLayer", "layer", "Lcom/chibidoll/dressup/domain/model/Layer;", "(Lcom/chibidoll/dressup/domain/model/Layer;)Ljava/lang/Long;", "getDetailedRenderLayers", "", "Lcom/chibidoll/dressup/domain/model/RenderLayerInfo;", "getRenderLayers", "Lkotlin/Pair;", "hashCode", "isEquipped", "item", "toString", "withLayerColor", "color", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$app_release", "$serializer", "Companion", "app_release"})
public final class CharacterLook {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.chibidoll.dressup.domain.model.CatalogItem> equippedItems = null;
    @org.jetbrains.annotations.Nullable()
    private final com.chibidoll.dressup.domain.model.CatalogItem backgroundItem = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Long> layerColors = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String presetId = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.domain.model.CharacterLook.Companion Companion = null;
    
    public CharacterLook(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, com.chibidoll.dressup.domain.model.CatalogItem> equippedItems, @org.jetbrains.annotations.Nullable()
    com.chibidoll.dressup.domain.model.CatalogItem backgroundItem, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Long> layerColors, @org.jetbrains.annotations.Nullable()
    java.lang.String presetId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, com.chibidoll.dressup.domain.model.CatalogItem> getEquippedItems() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.chibidoll.dressup.domain.model.CatalogItem getBackgroundItem() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Long> getLayerColors() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPresetId() {
        return null;
    }
    
    /**
     * Returns list of (Layer, relativeFilePath) ordered strictly by Layer.zIndex
     * from bottom to top:
     * hair_back -> body -> shoes -> bottom -> top -> dress -> outerwear -> face -> hair_front -> hat -> accessory.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<kotlin.Pair<com.chibidoll.dressup.domain.model.Layer, java.lang.String>> getRenderLayers() {
        return null;
    }
    
    /**
     * Returns rich layer render details including file path, color tint, and colorable flag.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.chibidoll.dressup.domain.model.RenderLayerInfo> getDetailedRenderLayers() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getColorForLayer(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.Layer layer) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.domain.model.CharacterLook withLayerColor(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.Layer layer, long color) {
        return null;
    }
    
    public final boolean isEquipped(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CatalogItem item) {
        return false;
    }
    
    public CharacterLook() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, com.chibidoll.dressup.domain.model.CatalogItem> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.chibidoll.dressup.domain.model.CatalogItem component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Long> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.domain.model.CharacterLook copy(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, com.chibidoll.dressup.domain.model.CatalogItem> equippedItems, @org.jetbrains.annotations.Nullable()
    com.chibidoll.dressup.domain.model.CatalogItem backgroundItem, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Long> layerColors, @org.jetbrains.annotations.Nullable()
    java.lang.String presetId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    public static final void write$Self$app_release(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook self, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/chibidoll/dressup/domain/model/CharacterLook.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_release"})
    @java.lang.Deprecated()
    public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.chibidoll.dressup.domain.model.CharacterLook> {
        @org.jetbrains.annotations.NotNull()
        public static final com.chibidoll.dressup.domain.model.CharacterLook.$serializer INSTANCE = null;
        
        private $serializer() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] childSerializers() {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.chibidoll.dressup.domain.model.CharacterLook deserialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Decoder decoder) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
            return null;
        }
        
        @java.lang.Override()
        public void serialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
        com.chibidoll.dressup.domain.model.CharacterLook value) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/chibidoll/dressup/domain/model/CharacterLook$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.serialization.KSerializer<com.chibidoll.dressup.domain.model.CharacterLook> serializer() {
            return null;
        }
    }
}