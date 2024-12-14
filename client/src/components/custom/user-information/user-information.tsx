import { UserPageOutletContext } from '@/pages/profile/profile';
import { UserUpdatedData } from '@/types';
import {
  FormControl,
  FormErrorMessage,
  FormLabel,
} from '@chakra-ui/form-control';
import { Button, Flex, Heading, Input } from '@chakra-ui/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useOutletContext } from 'react-router-dom';
import { toast } from 'react-toastify';
import styled from 'styled-components';

const StyledForm = styled('form')`
  width: 100%;
`;

export const UserInformation = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { user, updateUser, refreshUser } =
    useOutletContext<UserPageOutletContext>();
  const [isChanged, setIsChanged] = useState(false);

  const {
    handleSubmit,
    register,
    watch,
    formState: { errors },
  } = useForm<UserUpdatedData>({
    defaultValues: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      phoneNumber: user?.phoneNumber || '',
    },
  });

  const formValues = watch();

  useEffect(() => {
    function validateFormChange(values: UserUpdatedData) {
      return Object.entries(values).some(
        ([key, value]) => user[key as keyof typeof user] !== value,
      );
    }

    setIsChanged(validateFormChange(formValues));
  }, [formValues, user]);

  function onSubmit(values: UserUpdatedData) {
    setIsLoading(true);
    updateUser(values)
      .then(async () => {
        await refreshUser();
        toast.success('Profile updated successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to update profile, please try again in a few minutes',
        );
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  return (
    <Flex w="100%" maxW="730px" direction="column" gap={10}>
      <Heading fontFamily="inherit" size="2xl" color="textRaw">
        Profile
      </Heading>
      <StyledForm onSubmit={handleSubmit(onSubmit)}>
        <FormControl isInvalid={!!errors.firstName}>
          <FormLabel htmlFor="firstName">First name</FormLabel>
          <Input
            id="firstName"
            type="text"
            color="textRaw"
            _focus={{ outline: 'none' }}
            {...register('firstName', {
              required: 'First name is required',
              minLength: {
                value: 2,
                message: 'Minimum 2 characters',
              },
            })}
          />
          <FormErrorMessage mt={6} color="#cc0000" _dark={{ color: '#ff8080' }}>
            {errors.firstName?.message as string}
          </FormErrorMessage>
        </FormControl>
        <FormControl isInvalid={!!errors.lastName}>
          <FormLabel htmlFor="lastName" mt={16}>
            Last name
          </FormLabel>
          <Input
            id="lastName"
            type="text"
            color="textRaw"
            _focus={{ outline: 'none' }}
            {...register('lastName', {
              required: 'Last name is required',
              minLength: {
                value: 2,
                message: 'Minimum 2 characters',
              },
            })}
          />
          <FormErrorMessage mt={6} color="#cc0000" _dark={{ color: '#ff8080' }}>
            {errors.lastName?.message as string}
          </FormErrorMessage>
        </FormControl>
        <FormControl isInvalid={!!errors.phoneNumber}>
          <FormLabel htmlFor="phoneNumber" mt={16}>
            Phone number
          </FormLabel>
          <Input
            id="phoneNumber"
            type="tel"
            color="textRaw"
            css={{
              '-moz-appearance': 'textfield',
              '&::-webkit-inner-spin-button': { display: 'none' },
              '&::-webkit-outer-spin-button': { display: 'none' },
            }}
            _focus={{ outline: 'none' }}
            {...register('phoneNumber', {
              pattern: {
                value: /^[0-9]{9}$/,
                message: 'Invalid phone number, must be 9 digits',
              },
            })}
          />
          <FormErrorMessage mt={6} color="#cc0000" _dark={{ color: '#ff8080' }}>
            {errors.phoneNumber?.message as string}
          </FormErrorMessage>
        </FormControl>
        <Button
          fontSize="lg"
          bg="primary"
          color="textBg"
          _hover={{ bg: 'hover' }}
          disabled={isLoading || !isChanged}
          type="submit"
          mt={8}
        >
          Submit
        </Button>
      </StyledForm>
    </Flex>
  );
};
