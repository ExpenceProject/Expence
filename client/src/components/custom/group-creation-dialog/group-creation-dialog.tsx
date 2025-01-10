import GroupIcon from '@/assets/images/group_icon.svg';
import { CameraIcon } from '@/components/icons/camera';
import { Avatar } from '@/components/ui/avatar';
import { useColorModeValue } from '@/components/ui/color-mode';
import { GroupWithMembers, User } from '@/types';
import {
  closeGroupCreationModalAtom,
  isGroupCreationModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  FormControl,
  FormErrorMessage,
  FormLabel,
} from '@chakra-ui/form-control';
import {
  Box,
  Button,
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  Flex,
  Input,
  Text,
} from '@chakra-ui/react';
import { useAtom } from 'jotai';
import {
  Dispatch,
  FC,
  SetStateAction,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useForm } from 'react-hook-form';
import { MultiSelect, Option } from 'react-multi-select-component';
import { toast } from 'react-toastify';
import styled from 'styled-components';

type GroupCreationDialogProps = {
  getUserGroups: () => Promise<GroupWithMembers[]>;
  setUserGroups: Dispatch<SetStateAction<GroupWithMembers[]>>;
};

const StyledForm = styled('form')`
  width: 100%;
`;

export const GroupCreationDialog: FC<GroupCreationDialogProps> = ({
  getUserGroups,
  setUserGroups,
}) => {
  const [isGroupCreationModalOpen] = useAtom(isGroupCreationModalOpenAtom);
  const [, closeGroupCreationModal] = useAtom(closeGroupCreationModalAtom);
  const [isLoading, setIsLoading] = useState(false);
  const [uploadedImage, setUploadedImage] = useState<File | null>(null);
  const [uploadedImageUrl, setUploadedImageUrl] = useState<string>();
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<Option[]>([]);

  const { user } = useUser();

  const usersOptions = useMemo(() => {
    return users
      .filter((userOption) => userOption.id !== user?.id)
      .map((user) => {
        return { label: `${user.firstName} ${user.lastName}`, value: user.id };
      });
  }, [user, users]);

  const inputRef = useRef<HTMLInputElement>(null);
  const currentTheme = useColorModeValue('light', 'dark');

  const handleInputClick = () => {
    if (inputRef.current) {
      inputRef.current.click();
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      setUploadedImage(file);
      setUploadedImageUrl(URL.createObjectURL(file));
    }
  };

  const onSubmit = (data: { name: string }) => {
    setIsLoading(true);
    const groupData = new FormData();
    groupData.append('name', data.name);

    if (uploadedImage) {
      groupData.append('file', uploadedImage);
    }

    if (selectedUsers.length > 0) {
      groupData.append(
        'inviteesId',
        selectedUsers.map((user) => user.value).join(','),
      );
    }

    apiClient
      .post('/groups', groupData)
      .then(() => {
        getUserGroups().then((groups) => {
          setUserGroups(groups);
        });
        closeGroupCreationModal();
        toast.success('Group created successfully');
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        reset();
        setUploadedImage(null);
        setUploadedImageUrl(undefined);
        setSelectedUsers([]);
        setIsLoading(false);
      });
  };

  const {
    handleSubmit,
    register,
    reset,
    formState: { errors },
  } = useForm<{ name: string }>();

  useEffect(() => {
    apiClient
      .get('/users')
      .then((response) => {
        setUsers(response.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  useEffect(() => {
    let objectUrl: string | null = null;

    if (uploadedImage) {
      objectUrl = URL.createObjectURL(uploadedImage);
      setUploadedImageUrl(objectUrl);
    }

    return () => {
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, [uploadedImage]);

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isGroupCreationModalOpen}
      onInteractOutside={closeGroupCreationModal}
      onEscapeKeyDown={closeGroupCreationModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent
        shadow="xl"
        p={4}
        css={{
          position: 'absolute',
          left: '50%',
          transform: 'translateX(-50%)',
        }}
      >
        <DialogCloseTrigger
          _icon={{ w: 6, h: 6 }}
          onClick={closeGroupCreationModal}
        />
        <DialogHeader p={4}>
          <DialogTitle fontSize="xl">Create New Group</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={{ base: 2, md: 8 }}
        >
          <Flex
            direction="column"
            alignItems="center"
            pb={uploadedImage ? 4 : 8}
            w="100%"
          >
            <Box
              position="relative"
              w={200}
              h={200}
              display="flex"
              alignItems="center"
              justifyContent="center"
            >
              <Avatar
                src={uploadedImage ? uploadedImageUrl : GroupIcon}
                variant="solid"
                color="textBg"
                bg="primary"
                contain="content"
                w="200px"
                h="200px"
                className="modalAvatar"
                outlineWidth={4}
                outlineColor="primary"
                outlineOffset={-1}
                outlineStyle="solid"
                p={uploadedImage ? 0 : 10}
              />
              <Box
                as="button"
                borderRadius="100%"
                padding={2}
                cursor="pointer"
                _hover={{
                  bg: 'hover',
                }}
                position="absolute"
                bottom={-1}
                right={3}
                bg="background"
                outlineWidth={2}
                outlineColor="primary"
                outlineOffset={-1}
                outlineStyle="solid"
                transition="all 0.15s ease"
                onClick={handleInputClick}
                className="camera-icon-group-creation"
              >
                <CameraIcon />
              </Box>
            </Box>
            {uploadedImage && (
              <Text
                truncate
                w="100%"
                pt={8}
                textAlign="center"
                className="source-group-creation"
              >
                Source: {uploadedImage.name}
              </Text>
            )}
          </Flex>
          <Input
            type="file"
            ref={inputRef}
            display="none"
            onChange={handleFileChange}
            accept="image/*"
            max={1}
          />
          <StyledForm onSubmit={handleSubmit(onSubmit)}>
            <Text fontSize="sm" color="text">
              Members
            </Text>
            <MultiSelect
              options={usersOptions}
              value={selectedUsers}
              onChange={setSelectedUsers}
              labelledBy="Select"
              className="multi-select"
              hasSelectAll={false}
              valueRenderer={(selected) => {
                return selected.length > 0
                  ? selected.map((option) => option.label).join(', ')
                  : 'Select members...';
              }}
              overrideStrings={{
                selectSomeItems: 'Select members...',
                allItemsAreSelected: 'All members are selected',
                search: 'Search',
                noOptions: 'No members found',
              }}
            />
            <FormControl isInvalid={!!errors.name} isRequired>
              <FormLabel htmlFor="name">Name</FormLabel>
              <Input
                id="name"
                type="text"
                color="textRaw"
                _focus={{ outline: 'none' }}
                {...register('name', {
                  required: 'Group name is required',
                  minLength: {
                    value: 2,
                    message: 'Minimum 2 characters',
                  },
                })}
              />
              <FormErrorMessage
                mt={6}
                color="#cc0000"
                _dark={{ color: '#ff8080' }}
              >
                {errors.name?.message as string}
              </FormErrorMessage>
            </FormControl>
            <Button
              fontSize="lg"
              bg="primary"
              color="textBg"
              _hover={{ bg: 'hover' }}
              disabled={isLoading}
              mt={8}
              type="submit"
            >
              Submit
            </Button>
          </StyledForm>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
