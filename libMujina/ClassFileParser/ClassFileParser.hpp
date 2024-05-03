#pragma once
#include <cstdint>
#include <string>
#include <vector>

#pragma pack( push, 1)

class ClassFileParser
{
public:
    ClassFileParser(uint8_t class_bytes[], size_t size);

    std::string get_class_name();
    std::string get_super_class_name();
    std::vector<std::string> get_interfaces_names();
    std::vector<std::string> get_cp_strings();
    std::string get_cp_bytes();
private:
    struct cp_info
    {
        enum Tag : unsigned char
        {
            CONSTANT_Utf8 = 1U,
            CONSTANT_Integer = 3U,
            CONSTANT_Float = 4U,
            CONSTANT_Long = 5U,
            CONSTANT_Double = 6U,
            CONSTANT_Class = 7U,
            CONSTANT_String = 8U,
            CONSTANT_Fieldref = 9U,
            CONSTANT_Methodref = 10U,
            CONSTANT_InterfaceMethodref = 11U,
            CONSTANT_NameAndType = 12U,
            CONSTANT_MethodHandle = 15U,
            CONSTANT_MethodType = 16U,
            CONSTANT_Dynamic = 17U,
            CONSTANT_InvokeDynamic = 18U,
            CONSTANT_Module = 19U,
            CONSTANT_Package = 20U
        };
        struct CONSTANT_Class_info
        {
            Tag tag;
            uint16_t name_index;
        };
        struct CONSTANT_Fieldref_info
        {
            Tag tag;
            uint16_t class_index;
            uint16_t name_and_type_index;
        };
        struct CONSTANT_Methodref_info
        {
            Tag tag;
            uint16_t class_index;
            uint16_t name_and_type_index;
        };
        struct CONSTANT_InterfaceMethodref_info
        {
            Tag tag;
            uint16_t class_index;
            uint16_t name_and_type_index;
        };
        struct CONSTANT_String_info
        {
            Tag tag;
            uint16_t string_index;
        };
        struct CONSTANT_Integer_info
        {
            Tag tag;
            uint32_t bytes;
        };
        struct CONSTANT_Float_info
        {
            Tag tag;
            uint32_t bytes;
        };
        struct CONSTANT_Long_info
        {
            Tag tag;
            uint32_t high_bytes;
            uint32_t low_bytes;
        };
        struct CONSTANT_Double_info
        {
            Tag tag;
            uint32_t high_bytes;
            uint32_t low_bytes;
        };
        struct CONSTANT_NameAndType_info
        {
            Tag tag;
            uint16_t name_index;
            uint16_t descriptor_index;
        };
        struct CONSTANT_Utf8_info
        {
            Tag tag;
            uint16_t length;
            uint8_t bytes[1];

            std::string to_string();
        };
        struct CONSTANT_MethodHandle_info
        {
            Tag tag;
            uint8_t reference_kind;
            uint16_t reference_index;
        };
        struct CONSTANT_MethodType_info
        {
            Tag tag;
            uint16_t descriptor_index;
        };
        struct CONSTANT_Dynamic_info
        {
            Tag tag;
            uint16_t bootstrap_method_attr_index;
            uint16_t name_and_type_index;
        };
        struct CONSTANT_InvokeDynamic_info
        {
            Tag tag;
            uint16_t bootstrap_method_attr_index;
            uint16_t name_and_type_index;
        };
        struct CONSTANT_Module_info
        {
            Tag tag;
            uint16_t name_index;
        };
        struct CONSTANT_Package_info
        {
            Tag tag;
            uint16_t name_index;
        };
        Tag tag;
        uint8_t info[1];
    };

    struct attribute_info
    {
        uint16_t attribute_name_index;
        uint32_t attribute_length;
        uint8_t info[1]; //attribute_length
    };

    struct field_info
    {
        uint16_t             access_flags;
        uint16_t             name_index;
        uint16_t             descriptor_index;
        uint16_t             attributes_count;
        attribute_info      attributes[1];
    };

    struct method_info
    {
        uint16_t    access_flags;
        uint16_t    name_index;
        uint16_t    descriptor_index;
        uint16_t    attributes_count;
        attribute_info attributes[1];
    };

    struct ClassFile
    {
        struct Part1
        {
            uint32_t             magic;
            uint16_t             minor_version;
            uint16_t             major_version;
            uint16_t             constant_pool_count; //-1
            cp_info              constant_pool[1];

            cp_info* get_cp_info_at(uint16_t index);
        };
        struct Part2
        {
            uint16_t             access_flags;
            uint16_t             this_class;
            uint16_t             super_class;
            uint16_t             interfaces_count;
            uint16_t             interfaces[1];
        };
        struct Part3
        {
            uint16_t             fields_count;
            field_info           fields[1];
        };
        struct Part4
        {
            uint16_t             methods_count;
            method_info          methods[1];
        };
        struct Part5
        {
            uint16_t             attributes_count;
            attribute_info       attributes[1];
        };
        uint32_t             magic;
        uint16_t             minor_version;
        uint16_t             major_version;
        uint16_t             constant_pool_count; //-1
        cp_info              constant_pool[1];
        uint16_t             access_flags;
        uint16_t             this_class;
        uint16_t             super_class;
        uint16_t             interfaces_count;
        uint16_t             interfaces[1];
        uint16_t             fields_count;
        field_info           fields[1];
        uint16_t             methods_count;
        method_info          methods[1];
        uint16_t             attributes_count;
        attribute_info       attributes[1];

        Part1* get_part1();
        Part2* get_part2();
        Part3* get_part3();
    };

    ClassFile* classFile;
    size_t size;

    template<std::integral T>
    inline static T byteswap(T value)
    {
        T new_value = 0;
        int i = 0;
        for (uint8_t* p1 = (uint8_t*)&value; p1 < (uint8_t*)(&value) + sizeof(T); ++p1, ++i)
            *((uint8_t*)(&new_value) + sizeof(T) - 1 - i) = *p1;
        return new_value;
    }
};

#pragma pack(pop)