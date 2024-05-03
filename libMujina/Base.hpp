#pragma once
#include <iostream>

class Base
{
public:
	Base();
	bool is_error() const;
	bool is_valid() const;
	operator bool() const;
protected:
	bool _is_error;
};