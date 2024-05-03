#include "Base.hpp"

Base::Base() :
	_is_error(true)
{
}

bool Base::is_error() const
{
	return this->_is_error;
}

bool Base::is_valid() const
{
	return this->_is_error == false;
}

Base::operator bool() const
{
	return this->_is_error == false;
}
