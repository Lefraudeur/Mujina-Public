#include "ClassFileParser.hpp"
#include <iostream>
#include <string>
#include <algorithm>

ClassFileParser::ClassFileParser(uint8_t class_bytes[], size_t size) :
	classFile((ClassFile*)class_bytes),
	size(size)
{
}

std::string ClassFileParser::get_class_name()
{
    ClassFile::Part1* part1 = classFile->get_part1();
    ClassFile::Part2* part2 = classFile->get_part2();
    cp_info::CONSTANT_Class_info* class_info = (cp_info::CONSTANT_Class_info*)(part1->get_cp_info_at(byteswap(part2->this_class)));
    cp_info::CONSTANT_Utf8_info* class_name = (cp_info::CONSTANT_Utf8_info*)(part1->get_cp_info_at(byteswap(class_info->name_index)));
    return class_name->to_string();
}

std::string ClassFileParser::get_super_class_name()
{
    ClassFile::Part1* part1 = classFile->get_part1();
    ClassFile::Part2* part2 = classFile->get_part2();
    uint16_t super_class = byteswap(part2->super_class);
    if (!super_class)
        return "";
    cp_info::CONSTANT_Class_info* class_info = (cp_info::CONSTANT_Class_info*)(part1->get_cp_info_at(super_class));
    cp_info::CONSTANT_Utf8_info* class_name = (cp_info::CONSTANT_Utf8_info*)(part1->get_cp_info_at(byteswap(class_info->name_index)));
    return class_name->to_string();
}

std::vector<std::string> ClassFileParser::get_interfaces_names()
{
    std::vector<std::string> names{};

    ClassFile::Part1* part1 = classFile->get_part1();
    ClassFile::Part2* part2 = classFile->get_part2();
    uint16_t interfaces_count = byteswap(part2->interfaces_count);
    names.reserve(interfaces_count);
    for (uint16_t i = 0U; i < interfaces_count; ++i)
    {
        cp_info::CONSTANT_Class_info* class_info = (cp_info::CONSTANT_Class_info*)part1->get_cp_info_at(byteswap(part2->interfaces[i]));
        cp_info::CONSTANT_Utf8_info* class_name = (cp_info::CONSTANT_Utf8_info*)(part1->get_cp_info_at(byteswap(class_info->name_index)));
        names.push_back(class_name->to_string());
    }

    return names;
}

std::vector<std::string> ClassFileParser::get_cp_strings()
{
    std::vector<std::string> cp_strings{};
    cp_info* cp = classFile->constant_pool;
    uint16_t count = byteswap(classFile->constant_pool_count);
    for (uint16_t i = 1; i < count; ++i)
    {
        cp_info::Tag tag = cp->tag;
        switch (tag)
        {
        case cp_info::Tag::CONSTANT_Utf8:
        {
            cp_info::CONSTANT_Utf8_info* info = (cp_info::CONSTANT_Utf8_info*)cp;
            cp_strings.push_back(std::string((char*)info->bytes, byteswap(info->length)));
            cp = (cp_info*)(info->bytes + byteswap(info->length));
            break;
        }
        case cp_info::Tag::CONSTANT_Integer:
        {
            cp_info::CONSTANT_Integer_info* info = (cp_info::CONSTANT_Integer_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Float:
        {
            cp_info::CONSTANT_Float_info* info = (cp_info::CONSTANT_Float_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Long:
        {
            cp_info::CONSTANT_Long_info* info = (cp_info::CONSTANT_Long_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Double:
        {
            cp_info::CONSTANT_Double_info* info = (cp_info::CONSTANT_Double_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Class:
        {
            cp_info::CONSTANT_Class_info* info = (cp_info::CONSTANT_Class_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_String:
        {
            cp_info::CONSTANT_String_info* info = (cp_info::CONSTANT_String_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Fieldref:
        {
            cp_info::CONSTANT_Fieldref_info* info = (cp_info::CONSTANT_Fieldref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Methodref:
        {
            cp_info::CONSTANT_Methodref_info* info = (cp_info::CONSTANT_Methodref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_InterfaceMethodref:
        {
            cp_info::CONSTANT_InterfaceMethodref_info* info = (cp_info::CONSTANT_InterfaceMethodref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_NameAndType:
        {
            cp_info::CONSTANT_NameAndType_info* info = (cp_info::CONSTANT_NameAndType_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_MethodHandle:
        {
            cp_info::CONSTANT_MethodHandle_info* info = (cp_info::CONSTANT_MethodHandle_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_MethodType:
        {
            cp_info::CONSTANT_MethodType_info* info = (cp_info::CONSTANT_MethodType_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Dynamic:
        {
            cp_info::CONSTANT_Dynamic_info* info = (cp_info::CONSTANT_Dynamic_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_InvokeDynamic:
        {
            cp_info::CONSTANT_InvokeDynamic_info* info = (cp_info::CONSTANT_InvokeDynamic_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Module:
        {
            cp_info::CONSTANT_Module_info* info = (cp_info::CONSTANT_Module_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Package:
        {
            cp_info::CONSTANT_Package_info* info = (cp_info::CONSTANT_Package_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        }
    }
    return cp_strings;
}

std::string ClassFileParser::get_cp_bytes()
{
    uint8_t* cp_start = (uint8_t*)classFile->constant_pool;
    uint8_t* cp_end = (uint8_t*)classFile->get_part1()->get_cp_info_at(byteswap(classFile->get_part1()->constant_pool_count));
    size_t size = cp_end - cp_start;
    return std::string((char*)cp_start, size);
}

ClassFileParser::ClassFile::Part1* ClassFileParser::ClassFile::get_part1()
{
	return (Part1*)this;
}

ClassFileParser::ClassFile::Part2* ClassFileParser::ClassFile::get_part2()
{
    return (Part2*)(get_part1()->get_cp_info_at(byteswap(get_part1()->constant_pool_count)));
}

ClassFileParser::ClassFile::Part3* ClassFileParser::ClassFile::get_part3()
{
    Part2* part2 = get_part2();
    return (Part3*)(&part2->interfaces[byteswap(part2->interfaces_count)]);
}

ClassFileParser::cp_info* ClassFileParser::ClassFile::Part1::get_cp_info_at(uint16_t index)
{
	cp_info* cp = this->constant_pool;
	for (uint16_t i = 1; i < index; ++i)
	{
		cp_info::Tag tag = cp->tag;
		switch (tag)
		{
        case cp_info::Tag::CONSTANT_Utf8:
        {
            cp_info::CONSTANT_Utf8_info* info = (cp_info::CONSTANT_Utf8_info*)cp;
            cp = (cp_info*)(info->bytes + byteswap(info->length));
            break;
        }
        case cp_info::Tag::CONSTANT_Integer:
        {
            cp_info::CONSTANT_Integer_info* info = (cp_info::CONSTANT_Integer_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Float:
        {
            cp_info::CONSTANT_Float_info* info = (cp_info::CONSTANT_Float_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Long:
        {
            cp_info::CONSTANT_Long_info* info = (cp_info::CONSTANT_Long_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Double:
        {
            cp_info::CONSTANT_Double_info* info = (cp_info::CONSTANT_Double_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Class:
        {
            cp_info::CONSTANT_Class_info* info = (cp_info::CONSTANT_Class_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_String:
        {
            cp_info::CONSTANT_String_info* info = (cp_info::CONSTANT_String_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Fieldref:
        {
            cp_info::CONSTANT_Fieldref_info* info = (cp_info::CONSTANT_Fieldref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Methodref:
        {
            cp_info::CONSTANT_Methodref_info* info = (cp_info::CONSTANT_Methodref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_InterfaceMethodref:
        {
            cp_info::CONSTANT_InterfaceMethodref_info* info = (cp_info::CONSTANT_InterfaceMethodref_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_NameAndType:
        {
            cp_info::CONSTANT_NameAndType_info* info = (cp_info::CONSTANT_NameAndType_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_MethodHandle:
        {
            cp_info::CONSTANT_MethodHandle_info* info = (cp_info::CONSTANT_MethodHandle_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_MethodType:
        {
            cp_info::CONSTANT_MethodType_info* info = (cp_info::CONSTANT_MethodType_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Dynamic:
        {
            cp_info::CONSTANT_Dynamic_info* info = (cp_info::CONSTANT_Dynamic_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_InvokeDynamic:
        {
            cp_info::CONSTANT_InvokeDynamic_info* info = (cp_info::CONSTANT_InvokeDynamic_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Module:
        {
            cp_info::CONSTANT_Module_info* info = (cp_info::CONSTANT_Module_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
        case cp_info::Tag::CONSTANT_Package:
        {
            cp_info::CONSTANT_Package_info* info = (cp_info::CONSTANT_Package_info*)cp;
            cp = (cp_info*)(info + 1);
            break;
        }
		}
	}
	return cp;
}

std::string ClassFileParser::cp_info::CONSTANT_Utf8_info::to_string()
{
    return std::string((char*)bytes, byteswap(length));
}
